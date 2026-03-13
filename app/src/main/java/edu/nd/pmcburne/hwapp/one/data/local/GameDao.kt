package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query(
        "SELECT * FROM games " +
                "WHERE dateEpochDay = :dateEpochDay AND gender = :gender " +
                "ORDER BY startTimeDisplay"
    )
    fun getGamesForDateAndGender(
        dateEpochDay: Long,
        gender: String
    ): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGames(games: List<GameEntity>)
}

