package edu.nd.pmcburne.hwapp.one.ui

import edu.nd.pmcburne.hwapp.one.domain.Game
import edu.nd.pmcburne.hwapp.one.domain.Gender
import java.time.LocalDate

data class ScoresUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedGender: Gender = Gender.MEN,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val games: List<Game> = emptyList()
)

