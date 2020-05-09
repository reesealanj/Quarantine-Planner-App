package com.example.quarantineplanner

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync

class ResultsActivity : AppCompatActivity() {

    private lateinit var prefs: Preferences
    private lateinit var mediaIcon: ImageView
    private lateinit var mediaTitle: TextView
    private lateinit var mediaDescription: TextView
    private lateinit var mealTitle: TextView
    private lateinit var mealIcon: ImageView
    private lateinit var mealCookTime: TextView
    private lateinit var mealMakesLabel: TextView
    private lateinit var mealLinkButton: ImageButton
    private lateinit var redoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        // preferences from last screen to be used as data for network calls
        prefs = intent.getSerializableExtra("prefs_object") as Preferences
        returnRecommendations()

        mealTitle = findViewById(R.id.mealTitle)
        mealIcon = findViewById(R.id.mealIcon)
        mealCookTime = findViewById(R.id.mealCookTime)
        mealMakesLabel = findViewById(R.id.makesLabel)
        mealLinkButton = findViewById(R.id.mealLinkButton)
        mediaIcon = findViewById(R.id.mediaPoster)
        mediaTitle = findViewById(R.id.mediaTitle)
        mediaDescription = findViewById(R.id.mediaDescription)
        redoButton = findViewById(R.id.redoButton)
        redoButton.setOnClickListener{
            val intent: Intent = Intent(this, MediaSelectActivity::class.java)
            startActivity(intent)
        }
    }

    fun returnRecommendations(){
        doAsync {
            val manager = DetailsManager()
            val foodAPIKey = getString(R.string.edamam_key)
            val foodAPIID = getString(R.string.edamam_id)
            val movieAPIKey = getString(R.string.movie_db_key)

            try {
                val results = manager.detailRetrieval(prefs, foodAPIID, foodAPIKey, movieAPIKey)

                runOnUiThread {
                    Log.i("ResultsActivity", results.toString())
                    //Toast.makeText(this@ResultsActivity, "Returned Recipe Information", Toast.LENGTH_LONG).show()
                    mealTitle.text = results.meal_title
                    if(results.meal_cookTime > 0) {
                        mealCookTime.text = getString(R.string.cooking_time, results.meal_cookTime)
                    }
                    else {
                        mealCookTime.visibility = View.INVISIBLE
                    }
                    if(results.makes > 0) {
                        mealMakesLabel.text = getString(R.string.total_yield, results.makes.toInt())
                    }
                    else {
                        mealMakesLabel.visibility = View.INVISIBLE
                    }

                    if(results.meal_source_url.isEmpty()) {
                        mealLinkButton.visibility = View.GONE
                    } else {
                        mealLinkButton.setOnClickListener{
                            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(results.meal_source_url))
                            this@ResultsActivity.startActivity(intent)
                        }
                    }
                    if(results.meal_image_url.isNotEmpty()) {
                        Picasso.get()
                            .load(results.meal_image_url)
                            .resize(150,150)
                            .centerCrop()
                            .into(mealIcon)
                    } else {
                        Toast.makeText(this@ResultsActivity, "Failed to load Photo!", Toast.LENGTH_LONG).show()
                    }

                    if(results.movie_poster.isNotEmpty()) {
                        Picasso.get()
                            .load(results.movie_poster)
                            .resize(154,231)
                            .centerCrop()
                            .into(mediaIcon)
                    } else {
                        Toast.makeText(this@ResultsActivity, "Failed to load Photo!", Toast.LENGTH_LONG).show()
                    }

                    mediaTitle.text = results.movie_title
                    mediaDescription.text = results.movie_description
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    Log.e("ResultsActivity", exception.toString())
                    Toast.makeText(this@ResultsActivity, "Failed to return Recipe information", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}
