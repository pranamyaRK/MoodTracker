package com.example.moodtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        val usernameRegister = findViewById<EditText>(R.id.UsernameRegister)
        val phoneRegister = findViewById<EditText>(R.id.PhoneNumberRegister)
        val emailRegister = findViewById<EditText>(R.id.EmailRegister)
        val passwordRegister = findViewById<EditText>(R.id.PasswordRegister)
        val confirmRegister = findViewById<EditText>(R.id.ConfirmRegister)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val redirectToLogin = findViewById<TextView>(R.id.RedirectToLogin)

        redirectToLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val username = usernameRegister.text.toString()
            val password = passwordRegister.text.toString()
            val phoneNumber = phoneRegister.text.toString()
            val email = emailRegister.text.toString()
            val confirmPass = confirmRegister.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty() &&
                phoneNumber.isNotEmpty() && email.isNotEmpty() && password == confirmPass) {
                if(databaseHelper.checkUser(username, password)) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                }
                val result = databaseHelper.insertUser(username, phoneNumber, email, password)
                if (result != -1L) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
