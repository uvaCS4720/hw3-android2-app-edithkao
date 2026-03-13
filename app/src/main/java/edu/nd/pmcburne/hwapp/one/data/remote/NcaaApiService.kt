package edu.nd.pmcburne.hwapp.one.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface NcaaApiService {

    @GET("scoreboard/{sport}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("sport") sport: String,
        @Path("year") year: Int,
        @Path("month") month: String,
        @Path("day") day: String
    ): ScoreboardResponseDto
}

