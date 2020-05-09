package com.example.quarantineplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MediaSelectActivity : AppCompatActivity() {

    private lateinit var mediaButtons: RadioGroup
    private lateinit var mediaGenre: Spinner
    private lateinit var mealType: Spinner
    private lateinit var proteinType: Spinner
    private lateinit var dairyToggle: Switch
    private lateinit var glutenToggle: Switch
    private lateinit var veganToggle: Switch
    private lateinit var submitButton: Button
    private lateinit var movieButton: RadioButton
    private lateinit var tvButton: RadioButton

    private var mediaGenreText : String = ""
    private var mediaTypeText: String = ""
    private var mealTypeText: String = ""
    private var proteinTypeText: String = ""
    private var dairyValue: Boolean = false
    private var glutenValue: Boolean = false
    private var veganValue: Boolean = false

    private var mediaTypeDone: Boolean = false
    private var mediaGenreDone: Boolean = false
    private var mealTypeDone: Boolean = false
    private var proteinTypeDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_select)

        mediaButtons = findViewById(R.id.mediaRadioGroup)
        mediaGenre = findViewById(R.id.movieGenreSpinner)
        mealType = findViewById(R.id.foodMealSpinner)
        proteinType = findViewById(R.id.proteinSpinner)
        dairyToggle = findViewById(R.id.dairyFreeSwitch)
        glutenToggle = findViewById(R.id.glutenFreeSwitch)
        veganToggle = findViewById(R.id.veganSwitch)
        submitButton = findViewById(R.id.submitButton)
        movieButton = findViewById(R.id.movieButton)
        tvButton = findViewById(R.id.tvButton)

        enableSubmit()

        ArrayAdapter.createFromResource(this, R.array.media_genres, android.R.layout.simple_spinner_item).also {
            adapter-> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mediaGenre.adapter = adapter
        }
        ArrayAdapter.createFromResource(this, R.array.meal_types, android.R.layout.simple_spinner_item).also {
                adapter-> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mealType.adapter = adapter
        }
        ArrayAdapter.createFromResource(this, R.array.protien_types, android.R.layout.simple_spinner_item).also {
                adapter-> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            proteinType.adapter = adapter
        }

        mediaGenre.onItemSelectedListener = spinnerWatcher
        mealType.onItemSelectedListener = spinnerWatcher
        proteinType.onItemSelectedListener = spinnerWatcher

        submitButton.setOnClickListener {
            if (mealTypeText == "Desayuno") {
                mealTypeText = "Breakfast"
            }
            if (mealTypeText == "Almuerzo") {
                mealTypeText = "Lunch"
            }
            if (mealTypeText == "Cena") {
                mealTypeText = "Dinner"
            }
            if (proteinTypeText == "Carne de vaca") {
                proteinTypeText = "Beef"
            }
            if (proteinTypeText == "Cerdo") {
                proteinTypeText = "Pork"
            }
            if (proteinTypeText == "Pollo") {
                proteinTypeText = "Chicken"
            }
            if (proteinTypeText == "Pavo") {
                proteinTypeText = "Turkey"
            }
            if (proteinTypeText == "Vegeteriano") {
                proteinTypeText = "Vegetarian"
            }
            val prefs: Preferences = Preferences(mediaTypeText, mediaGenreText, mealTypeText, proteinTypeText, dairyValue, glutenValue, veganValue)
            val intent: Intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("prefs_object", prefs)
            startActivity(intent)
        }

        mediaButtons.setOnCheckedChangeListener { group: RadioGroup, checked: Int ->
            if(checked == R.id.movieButton) {
                mediaTypeDone = true
                mediaTypeText = "movie"
            } else if(checked == R.id.tvButton) {
                mediaTypeDone = true
                mediaTypeText = "tv"
            }
        }

        dairyToggle.setOnCheckedChangeListener { _ , isChecked ->
            dairyValue = isChecked
        }

        glutenToggle.setOnCheckedChangeListener { _ , isChecked ->
            glutenValue = isChecked
        }

        veganToggle.setOnCheckedChangeListener { _ , isChecked ->
            veganValue = isChecked
        }
    }

    private val spinnerWatcher = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
            if(parent.id == R.id.movieGenreSpinner) {
                mediaGenreDone = position > 0
                if (position > 0) {
                    mediaGenreText = parent.getItemAtPosition(position).toString()
                }
            } else if (parent.id == R.id.foodMealSpinner) {
                mealTypeDone = position > 0
                if(position > 0) {
                    mealTypeText = parent.getItemAtPosition(position).toString()
                }
            } else if (parent.id == R.id.proteinSpinner) {
                proteinTypeDone = position > 0
                if(position > 0) {
                    proteinTypeText = parent.getItemAtPosition(position).toString()
                }
                // You can't ask for a protein and vegan, they don't mix. duh.
                veganToggle.isEnabled = !(position != 0 && position != 5 && position != 6)
            }
            enableSubmit()
        }
    }

    fun enableSubmit() {
        submitButton.isEnabled = proteinTypeDone && mealTypeDone && mediaGenreDone && mediaTypeDone
    }
}
