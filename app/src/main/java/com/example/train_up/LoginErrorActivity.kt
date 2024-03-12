package com.example.train_up

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_error)

        val errorMessage = findViewById<TextView>(R.id.textView4)

        errorMessage.text = intent.getStringExtra("message")

        val forgotPasswordButton: Button = findViewById(R.id.forgot_password)

        forgotPasswordButton.setOnClickListener {
            val nextIntent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(nextIntent)
        }

        val createAccountButton: Button = findViewById(R.id.create_account_button)

        createAccountButton.setOnClickListener {
            val nextIntent = Intent(this, CreateAccountActivity::class.java)
            startActivity(nextIntent)
        }

        val back: Button = findViewById(R.id.return_button)

        back.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }
}