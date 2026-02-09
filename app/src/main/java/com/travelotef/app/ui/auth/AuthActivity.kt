package com.travelotef.app.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.travelotef.app.MainActivity
import com.travelotef.app.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Authentication Activity
 * Hosts the auth navigation graph (login, register, forgot password)
 * Redirects to MainActivity on successful authentication
 */
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.auth_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    /**
     * Navigate to MainActivity after successful login
     */
    fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
