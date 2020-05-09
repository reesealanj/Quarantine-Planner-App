package com.example.quarantineplanner

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DetailsManager {

    private val okHttpClient: OkHttpClient
    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        builder.addInterceptor(loggingInterceptor)
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        okHttpClient = builder.build()
    }

    fun detailRetrieval(prefs: Preferences, foodApiID: String, foodAPIKey: String, movieAPIKey: String): Details {
        var urlEnding = "?app_id=${foodApiID}&app_key=${foodAPIKey}&to=1&q=${prefs.mealType}%7c${prefs.proteinType}"
        if(prefs.dairyFree) {
            urlEnding = "$urlEnding%7cdairy-free"
        }
        if(prefs.glutenFree) {
            urlEnding = "$urlEnding%7cgluten-free"
        }
        if(prefs.vegan) {
            urlEnding = "$urlEnding%7cvegan"
        }

        val request = Request.Builder()
            .url("https://api.edamam.com/search$urlEnding")
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseString: String? = response.body?.string()

        var mealTitle: String = ""
        var mealImage: String = ""
        var mealUrl: String = ""
        var mealMakes: Double = 0.0
        var mealTime: Int = 0

        if(response.isSuccessful) {
            val json = JSONObject(responseString)
            val responses = json.getJSONArray("hits")
            val mid = responses.getJSONObject(0)
            val recipe = mid.getJSONObject("recipe")
            mealTitle = recipe.getString("label")
            mealImage = recipe.getString("image")
            mealUrl = recipe.getString("url")
            mealMakes = recipe.getDouble("yield")
            mealTime = recipe.getInt("totalTime")
        }

        var urlEnding2 = "/${prefs.mediaType}?api_key=${movieAPIKey}&timezone=America%2FNew_York&sort_by=popularity.desc&language=en-US"

        if (prefs.mediaGenre == "Comedy" || prefs.mediaGenre == "Comedia") {
            urlEnding2 = "$urlEnding2&with_genres=35"
        } else if (prefs.mediaGenre == "Animated" || prefs.mediaGenre == "Animada") {
            urlEnding2 = "$urlEnding2&with_genres=16"
        } else if (prefs.mediaGenre == "Documentary" || prefs.mediaGenre == "Documental") {
            urlEnding2 = "$urlEnding2&with_genres=99"
        } else if (prefs.mediaGenre == "Drama") {
            urlEnding2 = "$urlEnding2&with_genres=18"
        } else if (prefs.mediaGenre == "Family" || prefs.mediaGenre == "Familia") {
            urlEnding2 = "$urlEnding2&with_genres=10751"
        } else if (prefs.mediaGenre == "Science Fiction" || prefs.mediaGenre == "Ciencia ficción") {
            urlEnding2 = "$urlEnding2&with_genres=878"
        }

        val request2 = Request.Builder()
            .url("https://api.themoviedb.org/3/discover$urlEnding2")
            .build()

        val response2 = okHttpClient.newCall(request2).execute()
        val responseString2: String? = response2.body?.string()

        var mediaTitle: String = ""
        var mediaDescription: String = ""
        var mediaPosterURL: String = "https://image.tmdb.org/t/p/w185"

        if(response2.isSuccessful) {
            val json = JSONObject(responseString2)
            val results = json.getJSONArray("results")
            val temp = results.getJSONObject(0)
            mediaTitle = if(prefs.mediaType == "movie") {
                temp.getString("original_title")
            } else {
                temp.getString("original_name")
            }

            mediaDescription = temp.getString("overview")
            mediaPosterURL = "$mediaPosterURL${temp.getString("poster_path")}"

        }
        return Details(mealTitle, mealImage, mealUrl, mealTime, mealMakes, mediaTitle, mediaPosterURL, mediaDescription)

    }
}