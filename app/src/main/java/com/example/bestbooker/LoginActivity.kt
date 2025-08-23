package com.example.bestbooker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bestbooker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val id = b.etEmailPhone.text.toString().trim()
            val pass = b.etPassword.text.toString()
            if (id.isNotEmpty() && pass.isNotEmpty()) {
                getSharedPreferences("user", MODE_PRIVATE).edit()
                    .putString("name", id.substringBefore("@", "User"))
                    .putString("email", if (id.contains("@")) id else "")
                    .putString("phone", if (!id.contains("@")) id else "")
                    .apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
