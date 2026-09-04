package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class QuranRepeatMode {
    CONTINUOUS_WHOLE_QURAN, // Automatically play next Surah when one ends
    REPEAT_SURAH,           // Loop the current Surah
    REPEAT_AYAH,            // Loop the current Ayah
    OFF                     // Stop after current track
}

data class QuranAudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurahNumber: Int? = null,
    val currentSurahEnglishName: String? = null,
    val currentSurahArabicName: String? = null,
    val currentAyahNumber: Int? = null,
    val currentTrackId: String? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val reciter: QariReciter = QuranReciters.DEFAULT_RECITER,
    val repeatMode: QuranRepeatMode = QuranRepeatMode.CONTINUOUS_WHOLE_QURAN,
    val sleepTimerMinutesRemaining: Int? = null,
    val errorMessage: String? = null
)

class AudioPlayerHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var sleepTimer: CountDownTimer? = null

    private val _playerState = MutableStateFlow(QuranAudioPlayerState())
    val playerState: StateFlow<QuranAudioPlayerState> = _playerState.asStateFlow()

    // Backwards compatibility flows
    val isPlaying: StateFlow<Boolean> get() = MutableStateFlow(_playerState.value.isPlaying).asStateFlow()
    val currentTrackId: StateFlow<String?> get() = MutableStateFlow(_playerState.value.currentTrackId).asStateFlow()

    // Callback for navigating or resolving next surah in continuous mode
    var onSurahAutoAdvance: ((Int) -> Unit)? = null

    private fun startMediaService() {
        try {
            val intent = android.content.Intent(context, QuranMediaService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playFullSurah(
        surahNumber: Int,
        surahEnglishName: String,
        surahArabicName: String,
        reciter: QariReciter = _playerState.value.reciter,
        startPositionMs: Long = 0L
    ) {
        stop()

        val url = reciter.getFullSurahAudioUrl(surahNumber)
        val trackId = "surah_$surahNumber"

        _playerState.value = _playerState.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentSurahNumber = surahNumber,
            currentSurahEnglishName = surahEnglishName,
            currentSurahArabicName = surahArabicName,
            currentAyahNumber = null,
            currentTrackId = trackId,
            reciter = reciter,
            currentPositionMs = startPositionMs,
            durationMs = 0L,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                val localFile = QuranAudioDownloader.getAudioFile(context, reciter.id, surahNumber)
                if (localFile != null) {
                    setDataSource(context, Uri.fromFile(localFile))
                } else {
                    setDataSource(context, Uri.parse(url))
                }
                setOnPreparedListener { mp ->
                    try {
                        if (startPositionMs > 0L && startPositionMs < mp.duration) {
                            mp.seekTo(startPositionMs.toInt())
                        }
                        applyPlaybackSpeed(_playerState.value.playbackSpeed)
                        mp.start()
                        _playerState.value = _playerState.value.copy(
                            isPlaying = true,
                            isBuffering = false,
                            durationMs = mp.duration.toLong(),
                            currentPositionMs = mp.currentPosition.toLong()
                        )
                        startProgressUpdates()
                    } catch (e: Exception) {
                        _playerState.value = _playerState.value.copy(
                            isPlaying = false,
                            isBuffering = false,
                            errorMessage = "Unable to start playback"
                        )
                    }
                }
                setOnCompletionListener {
                    handleSurahCompletion(surahNumber)
                }
                setOnErrorListener { _, what, extra ->
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Audio playback error ($what, $extra)"
                    )
                    stopProgressUpdates()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = e.localizedMessage ?: "Failed to load Surah audio"
            )
        }
    }

    fun playAyahAudio(
        surahNumber: Int,
        ayahNumber: Int,
        url: String,
        surahEnglishName: String = "Surah $surahNumber",
        surahArabicName: String = "",
        onComplete: () -> Unit = {}
    ) {
        stop()

        val trackId = "$surahNumber:$ayahNumber"
        _playerState.value = _playerState.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentSurahNumber = surahNumber,
            currentSurahEnglishName = surahEnglishName,
            currentSurahArabicName = surahArabicName,
            currentAyahNumber = ayahNumber,
            currentTrackId = trackId,
            currentPositionMs = 0L,
            durationMs = 0L,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(url))
                setOnPreparedListener { mp ->
                    applyPlaybackSpeed(_playerState.value.playbackSpeed)
                    mp.start()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = true,
                        isBuffering = false,
                        durationMs = mp.duration.toLong(),
                        currentPositionMs = mp.currentPosition.toLong()
                    )
                    startProgressUpdates()
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false
                    )
                    stopProgressUpdates()
                    onComplete()
                }
                setOnErrorListener { _, _, _ ->
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Ayah audio error"
                    )
                    stopProgressUpdates()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = e.localizedMessage
            )
        }
    }

    fun playAudio(url: String, trackId: String, onComplete: () -> Unit = {}) {
        stop()
        _playerState.value = _playerState.value.copy(
            currentTrackId = trackId,
            isBuffering = true,
            isPlaying = false
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(url))
                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = true,
                        isBuffering = false,
                        durationMs = mp.duration.toLong()
                    )
                    startProgressUpdates()
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        currentTrackId = null
                    )
                    stopProgressUpdates()
                    onComplete()
                }
                setOnErrorListener { _, _, _ ->
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        currentTrackId = null
                    )
                    stopProgressUpdates()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                currentTrackId = null
            )
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        try {
            if (mp.isPlaying) {
                mp.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
                stopProgressUpdates()
            } else {
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startProgressUpdates()
            }
        } catch (e: Exception) {
            // handle
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.pause()
                    _playerState.value = _playerState.value.copy(isPlaying = false)
                    stopProgressUpdates()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let { mp ->
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startProgressUpdates()
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.let { mp ->
                val clamped = positionMs.coerceIn(0L, mp.duration.toLong()).toInt()
                mp.seekTo(clamped)
                _playerState.value = _playerState.value.copy(currentPositionMs = clamped.toLong())
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun seekForward10s() {
        val current = _playerState.value.currentPositionMs
        seekTo(current + 10_000L)
    }

    fun seekRewind10s() {
        val current = _playerState.value.currentPositionMs
        seekTo((current - 10_000L).coerceAtLeast(0L))
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { mp ->
                    val params = mp.playbackParams
                    params.speed = speed
                    mp.playbackParams = params
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun setReciter(reciter: QariReciter) {
        val previousReciter = _playerState.value.reciter
        if (previousReciter.id == reciter.id) return

        _playerState.value = _playerState.value.copy(reciter = reciter)

        // If currently playing a Surah, restart with new reciter at same position
        val currentSurah = _playerState.value.currentSurahNumber
        val isCurrentlyPlaying = _playerState.value.isPlaying
        val currentPos = _playerState.value.currentPositionMs

        if (currentSurah != null && isCurrentlyPlaying) {
            val engName = _playerState.value.currentSurahEnglishName ?: "Surah $currentSurah"
            val arName = _playerState.value.currentSurahArabicName ?: ""
            playFullSurah(currentSurah, engName, arName, reciter, currentPos)
        }
    }

    fun setRepeatMode(mode: QuranRepeatMode) {
        _playerState.value = _playerState.value.copy(repeatMode = mode)
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimer?.cancel()
        sleepTimer = null

        if (minutes == null || minutes <= 0) {
            _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = null)
            return
        }

        _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = minutes)

        sleepTimer = object : CountDownTimer((minutes * 60 * 1000).toLong(), 60_000) {
            override fun onTick(millisUntilFinished: Long) {
                val minsLeft = (millisUntilFinished / 60_000).toInt() + 1
                _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = minsLeft)
            }

            override fun onFinish() {
                _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = null)
                pause()
            }
        }.start()
    }

    private fun handleSurahCompletion(currentSurahNumber: Int) {
        when (_playerState.value.repeatMode) {
            QuranRepeatMode.REPEAT_SURAH -> {
                // Replay same surah
                val engName = _playerState.value.currentSurahEnglishName ?: "Surah $currentSurahNumber"
                val arName = _playerState.value.currentSurahArabicName ?: ""
                playFullSurah(currentSurahNumber, engName, arName, _playerState.value.reciter, 0L)
            }
            QuranRepeatMode.CONTINUOUS_WHOLE_QURAN -> {
                // Advance to next Surah in Whole Quran
                val nextSurah = if (currentSurahNumber < 114) currentSurahNumber + 1 else 1
                onSurahAutoAdvance?.invoke(nextSurah)
            }
            else -> {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isBuffering = false,
                    currentPositionMs = _playerState.value.durationMs
                )
                stopProgressUpdates()
            }
        }
    }

    fun playAdhanAlarm(
        soundName: String = "Makkah Adhan",
        customUri: String? = null,
        volume: Float = 0.6f,
        onDiagnosticResult: ((Boolean, String?, String) -> Unit)? = null
    ) {
        stop()
        _playerState.value = _playerState.value.copy(
            currentTrackId = "fajr_alarm_$soundName",
            isPlaying = true
        )

        // Resolve audio source: custom file if valid, otherwise generated high-quality local alarm audio
        var targetUri: Uri? = null
        var resolvedLabel = soundName

        if (soundName == "Custom Sound" || soundName == "Custom Sound / Song") {
            val localCustomFile = java.io.File(context.filesDir, "custom_fajr_alarm.mp3")
            if (localCustomFile.exists() && localCustomFile.length() > 0) {
                targetUri = Uri.fromFile(localCustomFile)
                resolvedLabel = "Custom File (${localCustomFile.name})"
            } else if (!customUri.isNullOrEmpty()) {
                targetUri = Uri.parse(customUri)
                resolvedLabel = "Custom Audio ($customUri)"
            }
        }

        // Default or fallback to local generated alarm sound (offline-safe, instant load)
        if (targetUri == null) {
            val localAudio = LocalAlarmAudioGenerator.getOrGenerateAlarmAudio(context, soundName)
            if (localAudio.exists() && localAudio.length() > 0) {
                targetUri = Uri.fromFile(localAudio)
                resolvedLabel = "$soundName (High-Fidelity Audio)"
            } else {
                targetUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                    ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                resolvedLabel = "$soundName (System Sound)"
            }
        }

        try {
            // Request Audio Focus for Alarm
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager?.requestAudioFocus(
                    android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                        ).build()
                )
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    null,
                    android.media.AudioManager.STREAM_ALARM,
                    android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
                )
            }

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setVolume(volume, volume)
                isLooping = true

                setDataSource(context, targetUri)

                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.value = _playerState.value.copy(isPlaying = true)
                    onDiagnosticResult?.invoke(true, null, resolvedLabel)
                }
                setOnErrorListener { _, what, extra ->
                    val error = "MediaPlayer playback error ($what, $extra)"
                    _playerState.value = _playerState.value.copy(isPlaying = false, errorMessage = error)
                    onDiagnosticResult?.invoke(false, error, resolvedLabel)
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            // Automatic fallback to system alarm ringtone
            try {
                val fallbackUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                    ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    setVolume(volume, volume)
                    isLooping = true
                    setDataSource(context, fallbackUri)
                    setOnPreparedListener { mp ->
                        mp.start()
                        _playerState.value = _playerState.value.copy(isPlaying = true)
                        onDiagnosticResult?.invoke(true, null, "$resolvedLabel (System Fallback)")
                    }
                    prepareAsync()
                }
            } catch (ex: Exception) {
                val error = "Failed to load alarm audio: ${e.localizedMessage ?: e.javaClass.simpleName}"
                _playerState.value = _playerState.value.copy(isPlaying = false, errorMessage = error)
                onDiagnosticResult?.invoke(false, error, resolvedLabel)
            }
        }
    }

    fun setAlarmVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        startMediaService() // Launch Foreground Service for Dynamic Island/Notifications
        progressJob = scope.launch {
            while (isActive) {
                try {
                    mediaPlayer?.let { mp ->
                        var isCurrentlyPlaying = false
                        try {
                            isCurrentlyPlaying = mp.isPlaying
                        } catch (e: Exception) {
                            // ignore state exception
                        }
                        if (isCurrentlyPlaying) {
                            _playerState.value = _playerState.value.copy(
                                currentPositionMs = mp.currentPosition.toLong(),
                                durationMs = mp.duration.toLong().coerceAtLeast(1L),
                                isPlaying = true
                            )
                        }
                    }
                } catch (e: Exception) {
                    // ignore
                }
                delay(400)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    fun stop() {
        stopProgressUpdates()
        val player = mediaPlayer
        mediaPlayer = null
        if (player != null) {
            try {
                player.setOnPreparedListener(null)
                player.setOnCompletionListener(null)
                player.setOnErrorListener(null)
                var playing = false
                try {
                    playing = player.isPlaying
                } catch (e: Exception) {
                    // isPlaying throws in invalid/preparing/stopped states
                }
                if (playing) {
                    try {
                        player.stop()
                    } catch (e: Exception) {
                        // ignore state exception
                    }
                }
            } catch (e: Exception) {
                // ignore
            } finally {
                try {
                    player.reset()
                } catch (e: Exception) {
                    // ignore
                }
                try {
                    player.release()
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isBuffering = false,
            currentTrackId = null
        )
    }
}
