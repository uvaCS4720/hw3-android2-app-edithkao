package edu.nd.pmcburne.hwapp.one.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScoreboardResponseDto(
    @Json(name = "games") val games: List<GameWrapperDto>
)

@JsonClass(generateAdapter = true)
data class GameWrapperDto(
    @Json(name = "game") val game: GameDto
)

@JsonClass(generateAdapter = true)
data class GameDto(
    @Json(name = "gameID") val gameId: String,
    @Json(name = "away") val away: TeamDto,
    @Json(name = "home") val home: TeamDto,
    @Json(name = "startTime") val startTime: String?,
    @Json(name = "startTimeEpoch") val startTimeEpoch: String?,
    @Json(name = "startDate") val startDate: String?,
    @Json(name = "gameState") val gameState: String,
    @Json(name = "currentPeriod") val currentPeriod: String?,
    @Json(name = "contestClock") val contestClock: String?,
    @Json(name = "finalMessage") val finalMessage: String?
)

@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "score") val score: String?,
    @Json(name = "names") val names: TeamNamesDto,
    @Json(name = "winner") val winner: Boolean?
)

@JsonClass(generateAdapter = true)
data class TeamNamesDto(
    @Json(name = "short") val short: String,
    @Json(name = "full") val full: String
)

