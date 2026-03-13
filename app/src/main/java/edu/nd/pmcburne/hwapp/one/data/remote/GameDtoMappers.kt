package edu.nd.pmcburne.hwapp.one.data.remote

import edu.nd.pmcburne.hwapp.one.domain.Game
import edu.nd.pmcburne.hwapp.one.domain.GameMappers
import edu.nd.pmcburne.hwapp.one.domain.GameStatus
import edu.nd.pmcburne.hwapp.one.domain.Gender

fun GameDto.toDomain(gender: Gender): Game {
    val date = GameMappers.mapDateStringToLocalDate(startDate)
    val status = GameMappers.deriveStatus(gameState, finalMessage)

    val homeScoreInt = home.score?.toIntOrNull()
    val awayScoreInt = away.score?.toIntOrNull()

    val winnerName = when {
        home.winner == true -> home.names.short
        away.winner == true -> away.names.short
        status == GameStatus.FINAL && homeScoreInt != null && awayScoreInt != null -> {
            when {
                homeScoreInt > awayScoreInt -> home.names.short
                awayScoreInt > homeScoreInt -> away.names.short
                else -> null
            }
        }
        else -> null
    }

    val startTimeDisplay = GameMappers.formatStartTime(startTimeEpoch, startTime)
    val timeRemainingDisplay = GameMappers.formatTimeRemaining(status, currentPeriod, contestClock)

    return Game(
        gameId = gameId,
        date = date,
        gender = gender,
        homeTeamName = home.names.short,
        awayTeamName = away.names.short,
        homeScore = homeScoreInt,
        awayScore = awayScoreInt,
        status = status,
        startTimeDisplay = startTimeDisplay,
        currentPeriod = if (status == GameStatus.LIVE) currentPeriod else null,
        timeRemainingDisplay = timeRemainingDisplay,
        winnerTeamName = winnerName
    )
}

