package com.vadim.hasdfa.musicwave.repositories

import com.vadim.hasdfa.musicwave.models.TopArtists
import com.vadim.hasdfa.musicwave.utils.Constants
import okhttp3.Request

object NetworkRepository {

    suspend fun getTopAuthors(): TopArtists {
        val url = Constants.BASE_URL + "/2.0/?method=chart.gettopartists&api_key=${Constants.API_KEY}&format=json"

        val request = Request.Builder().get().url(url).build()
        val call = Apifactory.client.newCall(request)

        val response = call.execute()
        val body = response.body() ?: return TopArtists()
        val json = body.string()

        return Apifactory.gson.fromJson(
            json, TopArtists::class.java
        )
    }
}