package com.example.ui.screens.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicApp
import com.example.audio.AudioPlayerHelper
import com.example.audio.QariReciter
import com.example.audio.QuranAudioPlayerState
import com.example.audio.QuranRepeatMode
import com.example.data.local.entity.BookmarkEntity
import com.example.data.repository.Ayah
import com.example.data.repository.QuranRepository
import com.example.data.repository.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class QuranFilterTab {
    ALL,
    DOWNLOADED,
    MECCAN,
    MEDINAN
}

data class QuranUiState(
    val searchQuery: String = "",
    val activeFilterTab: QuranFilterTab = QuranFilterTab.ALL,
    val allSurahs: List<Surah> = emptyList(),
    val filteredSurahs: List<Surah> = emptyList(),
    val currentSurah: Surah? = null,
    val isCurrentSurahDownloaded: Boolean = false,
    val ayahsList: List<Ayah> = emptyList(),
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val currentPlayingAyahIndex: Int? = null,
    val isLoadingAyahs: Boolean = false,
    val downloadedSurahsCount: Int = 0,
    val isDownloadingSurah: Boolean = false,
    val playerState: QuranAudioPlayerState = QuranAudioPlayerState(),
    val showFullPlayerModal: Boolean = false,
    val showReciterPicker: Boolean = false,
    val showSpeedPicker: Boolean = false,
    val showSleepTimerDialog: Boolean = false
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val quranRepository: QuranRepository = (application as IslamicApp).quranRepository
    val audioPlayer: AudioPlayerHelper = (application as IslamicApp).audioPlayerHelper

    private val _uiState = MutableStateFlow(
        QuranUiState(
            allSurahs = quranRepository.surahsList,
            filteredSurahs = quranRepository.surahsList
        )
    )
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        // Observe Surahs reactively from Room database
        viewModelScope.launch {
            quranRepository.getAllSurahsFlow().collect { surahs ->
                val currentTab = _uiState.value.activeFilterTab
                val currentQuery = _uiState.value.searchQuery
                val filtered = filterSurahs(surahs, currentTab, currentQuery)
                _uiState.value = _uiState.value.copy(
                    allSurahs = surahs,
                    filteredSurahs = filtered
                )
            }
        }

        // Observe count of downloaded offline surahs
        viewModelScope.launch {
            quranRepository.getDownloadedSurahsCount().collect { count ->
                _uiState.value = _uiState.value.copy(downloadedSurahsCount = count)
            }
        }

        // Observe bookmarks
        viewModelScope.launch {
            quranRepository.getAllBookmarks().collect { list ->
                _uiState.value = _uiState.value.copy(bookmarks = list)
            }
        }

        // Observe player state
        viewModelScope.launch {
            audioPlayer.playerState.collect { pState ->
                _uiState.value = _uiState.value.copy(playerState = pState)
            }
        }

        // Configure auto-advance for continuous full Quran playback
        audioPlayer.onSurahAutoAdvance = { nextSurahNumber ->
            val nextSurah = quranRepository.getSurahByNumber(nextSurahNumber)
            if (nextSurah != null) {
                playSurahAudio(nextSurah.number)
            }
        }
    }

    fun getAllSurahs(): List<Surah> = _uiState.value.allSurahs.ifEmpty { quranRepository.surahsList }

    fun setFilterTab(tab: QuranFilterTab) {
        val filtered = filterSurahs(_uiState.value.allSurahs, tab, _uiState.value.searchQuery)
        _uiState.value = _uiState.value.copy(
            activeFilterTab = tab,
            filteredSurahs = filtered
        )
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = filterSurahs(_uiState.value.allSurahs, _uiState.value.activeFilterTab, query)
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredSurahs = filtered
        )
    }

    private fun filterSurahs(
        surahs: List<Surah>,
        tab: QuranFilterTab,
        query: String
    ): List<Surah> {
        val baseList = when (tab) {
            QuranFilterTab.ALL -> surahs
            QuranFilterTab.DOWNLOADED -> surahs.filter { it.isDownloaded }
            QuranFilterTab.MECCAN -> surahs.filter { it.revelationType.equals("Meccan", ignoreCase = true) }
            QuranFilterTab.MEDINAN -> surahs.filter { it.revelationType.equals("Medinan", ignoreCase = true) }
        }

        if (query.isBlank()) return baseList

        val q = query.trim().lowercase()
        return baseList.filter {
            it.englishName.lowercase().contains(q) ||
                    it.englishNameTranslation.lowercase().contains(q) ||
                    it.name.contains(q) ||
                    it.number.toString() == q
        }
    }

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            val surah = quranRepository.getSurahByNumber(surahNumber)
            val isDownloaded = quranRepository.isSurahDownloaded(surahNumber).firstOrNull() ?: (surah?.isDownloaded ?: false)

            _uiState.value = _uiState.value.copy(
                currentSurah = surah,
                isCurrentSurahDownloaded = isDownloaded,
                isLoadingAyahs = true
            )

            val ayahs = quranRepository.getAyahsForSurah(surahNumber)
            _uiState.value = _uiState.value.copy(
                ayahsList = ayahs,
                isLoadingAyahs = false
            )
        }
    }

    fun toggleOfflineDownload(surahNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDownloadingSurah = true)
            val currentDownloaded = _uiState.value.isCurrentSurahDownloaded
            if (currentDownloaded) {
                quranRepository.removeOfflineSurah(surahNumber)
                _uiState.value = _uiState.value.copy(
                    isCurrentSurahDownloaded = false,
                    isDownloadingSurah = false
                )
            } else {
                quranRepository.downloadSurahForOffline(surahNumber)
                _uiState.value = _uiState.value.copy(
                    isCurrentSurahDownloaded = true,
                    isDownloadingSurah = false
                )
            }
        }
    }

    fun playSurahAudio(surahNumber: Int, reciter: QariReciter? = null) {
        val surah = quranRepository.getSurahByNumber(surahNumber) ?: return
        val targetReciter = reciter ?: _uiState.value.playerState.reciter
        _uiState.value = _uiState.value.copy(currentPlayingAyahIndex = null)
        audioPlayer.playFullSurah(
            surahNumber = surah.number,
            surahEnglishName = surah.englishName,
            surahArabicName = surah.name,
            reciter = targetReciter
        )
    }

    fun playAyahAudio(index: Int) {
        val ayahs = _uiState.value.ayahsList
        val surah = _uiState.value.currentSurah
        if (index in ayahs.indices && surah != null) {
            val ayah = ayahs[index]
            _uiState.value = _uiState.value.copy(currentPlayingAyahIndex = index)
            audioPlayer.playAyahAudio(
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.numberInSurah,
                url = ayah.audioUrl,
                surahEnglishName = surah.englishName,
                surahArabicName = surah.name,
                onComplete = {
                    if (index + 1 < ayahs.size) {
                        playAyahAudio(index + 1)
                    } else {
                        _uiState.value = _uiState.value.copy(currentPlayingAyahIndex = null)
                    }
                }
            )
        }
    }

    fun togglePlayPause() {
        val playerState = _uiState.value.playerState
        if (playerState.currentTrackId == null && _uiState.value.currentSurah != null) {
            playSurahAudio(_uiState.value.currentSurah!!.number)
        } else if (playerState.currentTrackId == null && quranRepository.surahsList.isNotEmpty()) {
            playSurahAudio(1) // Play Al-Fatihah by default
        } else {
            audioPlayer.togglePlayPause()
        }
    }

    fun playNextSurah() {
        val current = _uiState.value.playerState.currentSurahNumber ?: (_uiState.value.currentSurah?.number ?: 1)
        val next = if (current < 114) current + 1 else 1
        playSurahAudio(next)
    }

    fun playPreviousSurah() {
        val current = _uiState.value.playerState.currentSurahNumber ?: (_uiState.value.currentSurah?.number ?: 1)
        val prev = if (current > 1) current - 1 else 114
        playSurahAudio(prev)
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun seekForward10s() {
        audioPlayer.seekForward10s()
    }

    fun seekRewind10s() {
        audioPlayer.seekRewind10s()
    }

    fun selectReciter(reciter: QariReciter) {
        audioPlayer.setReciter(reciter)
        _uiState.value = _uiState.value.copy(showReciterPicker = false)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
        _uiState.value = _uiState.value.copy(showSpeedPicker = false)
    }

    fun setRepeatMode(mode: QuranRepeatMode) {
        audioPlayer.setRepeatMode(mode)
    }

    fun toggleRepeatMode() {
        val current = _uiState.value.playerState.repeatMode
        val next = when (current) {
            QuranRepeatMode.CONTINUOUS_WHOLE_QURAN -> QuranRepeatMode.REPEAT_SURAH
            QuranRepeatMode.REPEAT_SURAH -> QuranRepeatMode.REPEAT_AYAH
            QuranRepeatMode.REPEAT_AYAH -> QuranRepeatMode.OFF
            QuranRepeatMode.OFF -> QuranRepeatMode.CONTINUOUS_WHOLE_QURAN
        }
        audioPlayer.setRepeatMode(next)
    }

    fun setSleepTimer(minutes: Int?) {
        audioPlayer.setSleepTimer(minutes)
        _uiState.value = _uiState.value.copy(showSleepTimerDialog = false)
    }

    fun setFullPlayerModalVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showFullPlayerModal = visible)
    }

    fun setReciterPickerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showReciterPicker = visible)
    }

    fun setSpeedPickerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showSpeedPicker = visible)
    }

    fun setSleepTimerDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showSleepTimerDialog = visible)
    }

    fun pauseAudio() {
        audioPlayer.pause()
        _uiState.value = _uiState.value.copy(currentPlayingAyahIndex = null)
    }

    fun stopAudio() {
        audioPlayer.stop()
        _uiState.value = _uiState.value.copy(currentPlayingAyahIndex = null)
    }

    fun toggleBookmark(ayah: Ayah) {
        val surah = _uiState.value.currentSurah ?: return
        val isBookmarked = isAyahBookmarked(surah.number, ayah.numberInSurah)
        viewModelScope.launch {
            quranRepository.toggleBookmark(surah, ayah, !isBookmarked)
        }
    }

    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        val refId = "$surahNumber:$ayahNumber"
        return _uiState.value.bookmarks.any { it.referenceId == refId && it.type == "QURAN_AYAH" }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
