package edu.nd.pmcburne.hwapp.one.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.nd.pmcburne.hwapp.one.domain.Game
import edu.nd.pmcburne.hwapp.one.domain.GameStatus
import edu.nd.pmcburne.hwapp.one.domain.Gender
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScoresRoute(
    viewModel: ScoresViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScoresScreen(
        state = uiState,
        onDateChanged = viewModel::onDateChanged,
        onGenderChanged = viewModel::onGenderChanged,
        onRefresh = viewModel::onManualRefresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresScreen(
    state: ScoresUiState,
    onDateChanged: (LocalDate) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onRefresh: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NCAA Scores") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FiltersRow(
                    date = state.selectedDate,
                    gender = state.selectedGender,
                    onDateChanged = onDateChanged,
                    onGenderChanged = onGenderChanged,
                    onRefresh = onRefresh
                )

                GamesList(
                    games = state.games,
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.isLoading || state.isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersRow(
    date: LocalDate,
    gender: Gender,
    onDateChanged: (LocalDate) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onRefresh: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Date", style = MaterialTheme.typography.labelMedium)
                Text(text = date.format(dateFormatter), style = MaterialTheme.typography.titleMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onDateChanged(date.minusDays(1)) }) {
                    Text("<")
                }
                Button(onClick = { onDateChanged(date.plusDays(1)) }) {
                    Text(">")
                }
            }
        }

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = gender == Gender.MEN,
                onClick = { onGenderChanged(Gender.MEN) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) {
                Text("Men")
            }
            SegmentedButton(
                selected = gender == Gender.WOMEN,
                onClick = { onGenderChanged(Gender.WOMEN) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) {
                Text("Women")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun GamesList(
    games: List<Game>,
    modifier: Modifier = Modifier
) {
    if (games.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No games available for this date.")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(games) { game ->
                GameRow(game = game)
            }
        }
    }
}

@Composable
private fun GameRow(game: Game) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TeamLine(
                    name = game.awayTeamName,
                    score = game.awayScore,
                    isWinner = game.winnerTeamName == game.awayTeamName
                )
                TeamLine(
                    name = game.homeTeamName,
                    score = game.homeScore,
                    isWinner = game.winnerTeamName == game.homeTeamName
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                when (game.status) {
                    GameStatus.UPCOMING -> {
                        Text(
                            text = game.startTimeDisplay,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    GameStatus.LIVE -> {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        game.timeRemainingDisplay?.let {
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    GameStatus.FINAL -> {
                        Text(
                            text = "Final",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamLine(
    name: String,
    score: Int?,
    isWinner: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
            )
        )
        score?.let {
            Text(
                text = it.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}
