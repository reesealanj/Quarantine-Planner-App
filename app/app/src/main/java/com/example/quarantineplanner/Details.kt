package com.example.quarantineplanner

import java.io.Serializable


data class Details (
    val meal_title: String,
    val meal_image_url: String,
    val meal_source_url: String,
    val meal_cookTime: Int,
    val makes: Double,
    val movie_title: String,
    val movie_poster: String,
    val movie_description: String
): Serializable
