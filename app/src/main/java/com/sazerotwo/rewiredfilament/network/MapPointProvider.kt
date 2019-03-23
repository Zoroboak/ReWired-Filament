package com.sazerotwo.rewiredfilament.network

import com.sazerotwo.rewiredfilament.model.MapPoint
import com.sazerotwo.rewiredfilament.model.MapPointList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapPointProvider {

    private var retrofit: Retrofit? = null
    private val BASE_URL = "https://psanchez.neocities.org"

    fun getMapPoints(completion: (List<MapPoint>) -> Unit) {
        val mapPointsService = getRetrofitInstance().create(GetMapPointsService::class.java)
        mapPointsService.getMapPoints().enqueue(object : Callback<MapPointList> {
            override fun onFailure(call: Call<MapPointList>, t: Throwable) {

            }

            override fun onResponse(call: Call<MapPointList>, response: Response<MapPointList>) {
                completion(response.body()?.points!!)
            }
        })
    }

    private fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}