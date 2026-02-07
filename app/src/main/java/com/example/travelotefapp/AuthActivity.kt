package com.example.travelotefapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelotefapp.ui.auth.LoginFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LoginFragment())
                .commit()
        }
    }
}