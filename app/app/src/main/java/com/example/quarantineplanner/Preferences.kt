package com.example.quarantineplanner

import java.io.Serializable

data class Preferences (
    val mediaType: String,
    val mediaGenre: String,
    val mealType: String,
    val proteinType: String,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val vegan: Boolean
): Serializable