package com.example.train_up

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth_success)

        val welcomeMessage = findViewById<TextView>(R.id.textView8)

        welcomeMessage.text = "Bienvenido(a), " + intent.getStringExtra("name")

        Handler().postDelayed({
            val nextIntent = Intent(this, MenuActivity::class.java)
            startActivity(nextIntent)
            finish()
        }, 2000)
    }

}