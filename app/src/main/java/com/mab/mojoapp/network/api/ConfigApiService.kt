package com.mab.mojoapp.network.api

import com.mab.mojoapp.network.entities.Members
import retrofit2.Response
import retrofit2.http.GET

interface ConfigApiService {

    @GET("mojo/team.json")
    suspend fun getAllTeamMembers(): Response<Members>

}