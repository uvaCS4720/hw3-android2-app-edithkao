package edu.nd.pmcburne.hwapp.one.data

import edu.nd.pmcburne.hwapp.one.data.local.GameDao
import edu.nd.pmcburne.hwapp.one.data.local.toDomain
import edu.nd.pmcburne.hwapp.one.data.local.toEntity
import edu.nd.pmcburne.hwapp.one.data.remote.NcaaApiService
import edu.nd.pmcburne.hwapp.one.data.remote.toDomain
import edu.nd.pmcburne.hwapp.one.domain.Game
import edu.nd.pmcburne.hwapp.one.domain.Gender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.Locale

class ScoresRepository(
    private val apiService: NcaaApiService,
    private val gameDao: GameDao
) {

    fun getScores(date: LocalDate, gender: Gender): Flow<List<Game>> {
        val epochDay = date.toEpochDay()
        return gameDao.getGamesForDateAndGender(
            dateEpochDay = epochDay,
            gender = gender.name
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun refreshScores(date: LocalDate, gender: Gender): Result<Unit> {
        return try {
            val sport = when (gender) {
                Gender.MEN -> "basketball-men"
                Gender.WOMEN -> "basketball-women"
            }

            val year = date.year
            val month = String.format(Locale.US, "%02d", date.monthValue)
            val day = String.format(Locale.US, "%02d", date.dayOfMonth)

            val response = apiService.getScoreboard(
                sport = sport,
                year = year,
                month = month,
                day = day
            )

            val games = response.games
                .map { it.game }
                .map { it.toDomain(gender) }

            val entities = games.map { it.toEntity() }
            gameDao.upsertGames(entities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

