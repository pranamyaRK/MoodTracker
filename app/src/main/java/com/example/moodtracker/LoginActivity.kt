package com.example.moodtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)

        val etUsernameLogin = findViewById<EditText>(R.id.UsernameLogin)
        val etPasswordLogin = findViewById<EditText>(R.id.PasswordLogin)
        val btnLogin = findViewById<Button>(R.id.LoginButton)
        val redirectToRegister = findViewById<TextView>(R.id.RedirectToRegister)

        redirectToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = etUsernameLogin.text.toString()
            val password = etPasswordLogin.text.toString()

            if (databaseHelper.checkUser(username, password)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // Proceed to another activity or functionality

                // Save the username to SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.apply()

                Log.d("LoginActivity", "Starting MainActivity")
                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)

                // Finish LoginActivity to prevent going back to it with the back button
                finish()
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
