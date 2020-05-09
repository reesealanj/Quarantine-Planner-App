package com.example.quarantineplanner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var login: Button
    private lateinit var create: Button
    private lateinit var progress: ProgressBar
    private lateinit var prefs: SharedPreferences

    private var email: Boolean = false
    private var pass: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Bind variables to layout component
        firebase = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.email)
        passInput = findViewById(R.id.password)
        login = findViewById(R.id.loginButton)
        create = findViewById(R.id.createButton)
        progress = findViewById(R.id.progressBar)
        login.isEnabled = false

        // Add textWatcher to email and password field
        emailInput.addTextChangedListener(textWatcher)
        passInput.addTextChangedListener(textWatcher)

        // Add On Click Listener to create account button
        create.setOnClickListener {
            Log.i("LoginActivity", "Create Account Button Clicked")
            val intent: Intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            Log.i("LoginActivity", "Login Button Clicked")

            val inputtedUsername: String = emailInput.text.toString()
            val inputtedPassword: String = passInput.text.toString()

            firebase
                .signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        prefs
                            .edit()
                            .putString(R.string.saved_email.toString(), inputtedUsername)
                            .putString(R.string.saved_password.toString(), inputtedPassword)
                            .apply()

                        val intent: Intent = Intent(this, MediaSelectActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to sign in!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


        }

        //Initialize SharedPreferences
        prefs = getSharedPreferences(R.string.pref_file_name.toString(), Context.MODE_PRIVATE)
        emailInput.setText(prefs.getString(R.string.saved_email.toString(), ""))
        passInput.setText(prefs.getString(R.string.saved_password.toString(), ""))
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputtedEmail: String = emailInput.text.toString()
            val inputtedPassword: String = passInput.text.toString()

            email = inputtedEmail.trim().isNotEmpty()
            pass = inputtedPassword.trim().isNotEmpty()
            enableLogin()
        }

    }

    fun enableLogin() {
        login.isEnabled = email && pass
    }
}
