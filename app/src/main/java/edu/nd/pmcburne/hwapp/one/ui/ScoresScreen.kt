package edu.nd.pmcburne.hwapp.one.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ScoresScreen(
    state: ScoresUiState,
    onDateChanged: (LocalDate) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onRefresh: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(state.isRefreshing, onRefresh)

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("NCAA Scores") })
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FiltersRow(
                    date = state.selectedDate,
                    gender = state.selectedGender,
                    onDateChanged = onDateChanged,
                    onGenderChanged = onGenderChanged,
                    onRefresh = onRefresh
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pullRefresh(pullRefreshState)
                ) {
                    GamesList(games = state.games)
                    PullRefreshIndicator(
                        refreshing = state.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }

    if (showDatePicker) {
        val pickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            yearRange = (date.year - 2)..(date.year + 1)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChanged(selected)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.clickable { showDatePicker = true }) {
                Text(
                    text = "Date",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = { onDateChanged(date.minusDays(1)) }) { Text("<") }
                Button(onClick = { onDateChanged(date.plusDays(1)) }) { Text(">") }
            }
        }

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = gender == Gender.MEN,
                onClick = { onGenderChanged(Gender.MEN) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text("Men") }
            SegmentedButton(
                selected = gender == Gender.WOMEN,
                onClick = { onGenderChanged(Gender.WOMEN) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text("Women") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onRefresh) { Text("Refresh") }
        }
    }
}

@Composable
private fun GamesList(games: List<Game>, modifier: Modifier = Modifier) {
    if (games.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No games for this date.",
                style = MaterialTheme.typography.bodyMedium
            )
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TeamLine(
                        label = "Away",
                        name = game.awayTeamName,
                        score = game.awayScore,
                        isWinner = game.winnerTeamName == game.awayTeamName,
                        showScore = game.status != GameStatus.UPCOMING
                    )
                    TeamLine(
                        label = "Home",
                        name = game.homeTeamName,
                        score = game.homeScore,
                        isWinner = game.winnerTeamName == game.homeTeamName,
                        showScore = game.status != GameStatus.UPCOMING
                    )
                }
                Column(
                    modifier = Modifier.padding(start = 8.dp),
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
                            game.timeRemainingDisplay?.let { text ->
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        GameStatus.FINAL -> {
                            Text(
                                text = "Final",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            game.winnerTeamName?.let { winner ->
                                Text(
                                    text = "Winner: $winner",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamLine(
    label: String,
    name: String,
    score: Int?,
    isWinner: Boolean,
    showScore: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
        if (showScore && score != null) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}
