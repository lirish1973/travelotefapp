package com.example.travelotefapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.travelotefapp.AuthActivity
import com.example.travelotefapp.MainActivity
import com.example.travelotefapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Initialize Firebase Auth
        auth = Firebase.auth
        
        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 2000) // 2 seconds delay
    }

    private fun checkUserAndNavigate() {
        val currentUser = auth.currentUser
        
        val intent = if (currentUser != null) {
            // User is logged in, go to MainActivity
            Intent(this, MainActivity::class.java)
        } else {
            // User not logged in, go to AuthActivity
            Intent(this, AuthActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}