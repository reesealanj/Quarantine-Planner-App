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
import com.google.firebase.auth.*

class SignupActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var repPassInput: EditText
    private lateinit var back: Button
    private lateinit var create: Button
    private lateinit var progress: ProgressBar
    private lateinit var prefs: SharedPreferences


    private var email: Boolean = false
    private var pass: Boolean = false
    private var repPass: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebase = FirebaseAuth.getInstance()
        prefs = getSharedPreferences(R.string.pref_file_name.toString(), Context.MODE_PRIVATE)
        emailInput = findViewById(R.id.email)
        passInput = findViewById(R.id.password)
        repPassInput = findViewById(R.id.repPassword)
        back = findViewById(R.id.backButton)
        create = findViewById(R.id.submit)
        progress = findViewById(R.id.progressBar)

        back.setOnClickListener {
            Log.i("SignupActivity", "Return to Login Button Clicked")
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        create.setOnClickListener {
            if(verifyPassword()) {
                val email: String = emailInput.text.toString()
                val password: String = passInput.text.toString()
                // create account and do db stuff
                firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task: Task<AuthResult> ->

                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser = firebase.currentUser!!
                        val currentEmail = currentUser.email
                        Toast.makeText(this, "User successfully created with email: $currentEmail", Toast.LENGTH_LONG).show()
                        prefs
                            .edit()
                            .putString(R.string.saved_email.toString(), email)
                            .putString(R.string.saved_password.toString(), password)
                            .apply()


                        val intent: Intent = Intent(this, MediaSelectActivity::class.java)
                        startActivity(intent)
                    } else {
                        when (val exception: Exception = task.exception!!) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "Registration Failed! Your password needs to be 6 characters or more!", Toast.LENGTH_LONG).show()
                            }
                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(this, "Registration Failed! You've already registered!", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this, "Registration Failed! $exception", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }
                Toast.makeText(this, "User successfully created with email: $email", Toast.LENGTH_LONG).show()
            } else {
                // passwords don't match
                passInput.text.clear()
                repPassInput.text.clear()
                Toast.makeText(this, "Passwords must match!", Toast.LENGTH_SHORT).show()
            }
        }

        create.isEnabled = false
        emailInput.addTextChangedListener(textWatcher)
        passInput.addTextChangedListener(textWatcher)
        repPassInput.addTextChangedListener(textWatcher)
    }

    fun verifyPassword(): Boolean {
        return passInput.text.toString() == repPassInput.text.toString()
    }

    fun enableCreate() {
        create.isEnabled = email && pass && repPass
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputtedEmail: String = emailInput.text.toString()
            val inputtedPassword: String = passInput.text.toString()
            val inputtedRepeatPassword: String = repPassInput.text.toString()

            email = inputtedEmail.trim().isNotEmpty()
            pass = inputtedPassword.trim().isNotEmpty()
            repPass = inputtedRepeatPassword.trim().isNotEmpty()
            enableCreate()
        }
    }
}
