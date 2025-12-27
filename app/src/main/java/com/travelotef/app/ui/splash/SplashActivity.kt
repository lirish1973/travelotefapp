package com.travelotef.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.travelotef.app.MainActivity
import com.travelotef.app.R

/**
 * Splash Screen Activity
 * Shows app logo and checks user authentication status
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDuration = 2500L // 2.5 seconds
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        auth = FirebaseAuth.getInstance()
        
        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashDuration)
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        
        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}