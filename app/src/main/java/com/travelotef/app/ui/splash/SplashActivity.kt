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
 * Shows app logo, checks user authentication status,
 * and navigates to the appropriate screen.
 *
 * All users go to MainActivity (guest browsing supported).
 * Auth is optional - only needed for favorites sync across devices.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDuration = 2000L // 2 seconds
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
        // All users go to MainActivity - guest browsing is supported
        // Auth is optional for favorites/profile features
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("isLoggedIn", auth.currentUser != null)
        }
        startActivity(intent)
        finish()

        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
