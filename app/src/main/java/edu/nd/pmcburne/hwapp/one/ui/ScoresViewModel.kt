package edu.nd.pmcburne.hwapp.one.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.ScoresRepository
import edu.nd.pmcburne.hwapp.one.data.local.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.remote.NetworkModule
import edu.nd.pmcburne.hwapp.one.domain.Gender
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoresRepository by lazy {
        val db = AppDatabase.getInstance(application)
        ScoresRepository(
            apiService = NetworkModule.apiService,
            gameDao = db.gameDao()
        )
    }

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    private var gamesJob: Job? = null

    init {
        observeGames()
        refreshScores(initial = true)
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            errorMessage = null
        )
        observeGames()
        refreshScores()
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.value = _uiState.value.copy(
            selectedGender = gender,
            errorMessage = null
        )
        observeGames()
        refreshScores()
    }

    fun onManualRefresh() {
        refreshScores()
    }

    private fun observeGames() {
        gamesJob?.cancel()
        val state = _uiState.value
        gamesJob = viewModelScope.launch {
            repository.getScores(state.selectedDate, state.selectedGender)
                .collectLatest { games ->
                    _uiState.value = _uiState.value.copy(games = games)
                }
        }
    }

    private fun refreshScores(initial: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = initial,
                isRefreshing = !initial,
                errorMessage = null
            )
            val state = _uiState.value
            val result = repository.refreshScores(state.selectedDate, state.selectedGender)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                isOffline = result.isFailure,
                errorMessage = result.exceptionOrNull()?.localizedMessage
            )
        }
    }
}

