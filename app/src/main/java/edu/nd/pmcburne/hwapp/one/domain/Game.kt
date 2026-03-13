package edu.nd.pmcburne.hwapp.one.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class Gender {
    MEN,
    WOMEN
}

enum class GameStatus {
    UPCOMING,
    LIVE,
    FINAL
}

data class Game(
    val gameId: String,
    val date: LocalDate,
    val gender: Gender,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: GameStatus,
    val startTimeDisplay: String,
    val currentPeriod: String?,
    val timeRemainingDisplay: String?,
    val winnerTeamName: String?
)

object GameMappers {

    private val dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.US)
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

    fun mapDateStringToLocalDate(date: String?): LocalDate {
        return if (date.isNullOrBlank()) {
            LocalDate.now()
        } else {
            LocalDate.parse(date, dateFormatter)
        }
    }

    fun formatStartTime(epochString: String?, fallback: String?): String {
        val epochSeconds = epochString?.toLongOrNull()
        if (epochSeconds != null) {
            val instant = Instant.ofEpochSecond(epochSeconds)
            val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
            return localTime.format(timeFormatter)
        }
        return fallback ?: ""
    }

    fun deriveStatus(gameState: String, finalMessage: String?): GameStatus {
        return when (gameState.lowercase(Locale.US)) {
            "final" -> GameStatus.FINAL
            "pre" -> GameStatus.UPCOMING
            "in", "live" -> GameStatus.LIVE
            else -> {
                if ((finalMessage ?: "").uppercase(Locale.US).contains("FINAL")) {
                    GameStatus.FINAL
                } else {
                    GameStatus.UPCOMING
                }
            }
        }
    }

    fun formatTimeRemaining(
        status: GameStatus,
        currentPeriod: String?,
        contestClock: String?
    ): String? {
        return when (status) {
            GameStatus.FINAL -> "Final"
            GameStatus.UPCOMING -> null
            GameStatus.LIVE -> {
                val periodPart = currentPeriod ?: ""
                val clockPart = contestClock ?: ""
                listOf(periodPart, clockPart)
                    .filter { it.isNotBlank() }
                    .joinToString(" - ")
                    .ifBlank { null }
            }
        }
    }
}

