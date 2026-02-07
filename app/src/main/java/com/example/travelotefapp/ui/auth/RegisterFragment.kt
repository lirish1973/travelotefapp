package com.example.travelotefapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.travelotefapp.R
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    
   private lateinit var etEmail: TextInputEditText
private lateinit var etPassword: TextInputEditText
private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle? 
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnRegister = view.findViewById(R.id.btnRegister)
        tvLogin = view.findViewById(R.id.tvLogin)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            
            if (validateInput(email, password, confirmPassword)) {
                viewModel.signUpWithEmail(email, password)
            }
        }

        tvLogin.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "נא להזין אימייל"
            return false
        }
        
        if (! android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "אימייל לא תקין"
            return false
        }
        
        if (password.isEmpty()) {
            etPassword.error = "נא להזין סיסמה"
            return false
        }
        
        if (password.length < 6) {
            etPassword.error = "הסיסמה חייבת להכיל לפחות 6 תווים"
            return false
        }
        
        if (password != confirmPassword) {
            etConfirmPassword.error = "הסיסמאות אינן תואמות"
            return false
        }
        
        return true
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnRegister.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "רישום הצליח!  🎉", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.Idle -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}