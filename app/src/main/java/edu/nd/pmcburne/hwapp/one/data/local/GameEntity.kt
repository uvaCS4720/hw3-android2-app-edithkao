package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.nd.pmcburne.hwapp.one.domain.Game
import edu.nd.pmcburne.hwapp.one.domain.GameStatus
import edu.nd.pmcburne.hwapp.one.domain.Gender
import java.time.LocalDate

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val gameId: String,
    val dateEpochDay: Long,
    val gender: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: String,
    val startTimeDisplay: String,
    val currentPeriod: String?,
    val timeRemainingDisplay: String?,
    val winnerTeamName: String?
)

fun Game.toEntity(): GameEntity {
    return GameEntity(
        id = "${gameId}_${gender.name}",
        gameId = gameId,
        dateEpochDay = date.toEpochDay(),
        gender = gender.name,
        homeTeamName = homeTeamName,
        awayTeamName = awayTeamName,
        homeScore = homeScore,
        awayScore = awayScore,
        status = status.name,
        startTimeDisplay = startTimeDisplay,
        currentPeriod = currentPeriod,
        timeRemainingDisplay = timeRemainingDisplay,
        winnerTeamName = winnerTeamName
    )
}

fun GameEntity.toDomain(): Game {
    return Game(
        gameId = gameId,
        date = LocalDate.ofEpochDay(dateEpochDay),
        gender = Gender.valueOf(gender),
        homeTeamName = homeTeamName,
        awayTeamName = awayTeamName,
        homeScore = homeScore,
        awayScore = awayScore,
        status = GameStatus.valueOf(status),
        startTimeDisplay = startTimeDisplay,
        currentPeriod = currentPeriod,
        timeRemainingDisplay = timeRemainingDisplay,
        winnerTeamName = winnerTeamName
    )
}

