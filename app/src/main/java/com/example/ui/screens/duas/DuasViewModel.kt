package com.example.ui.screens.duas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicApp
import com.example.data.local.entity.DuaEntity
import com.example.data.repository.DuaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DuasUiState(
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val duasList: List<DuaEntity> = emptyList(),
    val featuredDua: DuaEntity? = null,
    val favoriteCount: Int = 0,
    val selectedDuaForDetail: DuaEntity? = null,
    val repetitionCounters: Map<String, Int> = emptyMap() // Map of duaId -> current count
)

class DuasViewModel(application: Application) : AndroidViewModel(application) {

    private val duaRepository: DuaRepository = (application as IslamicApp).duaRepository
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(
        DuasUiState(
            categories = duaRepository.categories,
            selectedCategory = "All"
        )
    )
    val uiState: StateFlow<DuasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            duaRepository.preloadDuasIfNeeded()
            observeFavoritesCount()
            loadDuas()
        }
    }

    private fun observeFavoritesCount() {
        viewModelScope.launch {
            duaRepository.getFavoriteCount().collectLatest { count ->
                _uiState.value = _uiState.value.copy(favoriteCount = count)
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadDuas()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadDuas()
    }

    fun selectQuickTag(tag: String) {
        val newQuery = if (_uiState.value.searchQuery.equals(tag, ignoreCase = true)) "" else tag
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
        loadDuas()
    }

    fun openDuaDetail(dua: DuaEntity?) {
        _uiState.value = _uiState.value.copy(selectedDuaForDetail = dua)
    }

    fun incrementDuaCounter(duaId: String, maxCount: Int = 33) {
        val current = _uiState.value.repetitionCounters[duaId] ?: 0
        val next = if (current + 1 > maxCount) 0 else current + 1
        _uiState.value = _uiState.value.copy(
            repetitionCounters = _uiState.value.repetitionCounters + (duaId to next)
        )
    }

    fun resetDuaCounter(duaId: String) {
        _uiState.value = _uiState.value.copy(
            repetitionCounters = _uiState.value.repetitionCounters - duaId
        )
    }

    private fun loadDuas() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val query = _uiState.value.searchQuery.trim()
            val category = _uiState.value.selectedCategory

            val flow = duaRepository.searchDuas(query, category)

            flow.collectLatest { list ->
                val featured = if (category == "All" && query.isBlank()) {
                    list.firstOrNull { it.id == "dua_morning_adhkar" } ?: list.firstOrNull()
                } else null

                _uiState.value = _uiState.value.copy(
                    duasList = list,
                    featuredDua = featured
                )
            }
        }
    }

    fun toggleFavorite(dua: DuaEntity) {
        viewModelScope.launch {
            duaRepository.toggleFavorite(dua.id, !dua.isFavorite)
        }
    }
}

