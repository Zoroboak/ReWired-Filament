package com.sazerotwo.rewiredfilament.network

import com.sazerotwo.rewiredfilament.model.MapPointList
import retrofit2.Call
import retrofit2.http.GET

interface GetMapPointsService {

    @GET("/geopoints.json")
    fun getMapPoints(): Call<MapPointList>
}
