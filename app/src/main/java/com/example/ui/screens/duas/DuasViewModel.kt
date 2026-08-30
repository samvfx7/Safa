package com.example.ui.screens.duas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicApp
import com.example.data.local.entity.DuaEntity
import com.example.data.repository.DuaRepository
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
    val favoriteCount: Int = 0
)

class DuasViewModel(application: Application) : AndroidViewModel(application) {

    private val duaRepository: DuaRepository = (application as IslamicApp).duaRepository

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
            loadDuas()
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadDuas()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadDuas()
    }

    private fun loadDuas() {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery.trim()
            val category = _uiState.value.selectedCategory

            val flow = if (query.isNotBlank()) {
                duaRepository.searchDuas(query)
            } else {
                duaRepository.getDuasByCategory(category)
            }

            flow.collectLatest { list ->
                val featured = list.firstOrNull { it.id == "dua_morning_adhkar" } ?: list.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    duasList = list,
                    featuredDua = featured,
                    favoriteCount = list.count { it.isFavorite }
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
