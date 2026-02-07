package com.example.travelotefapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.travelotefapp.R
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?  
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etEmail = view.findViewById(R.id.etEmail)
        btnResetPassword = view.findViewById(R.id.btnResetPassword)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnResetPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            
            if (validateInput(email)) {
                viewModel.resetPassword(email)
            }
        }
    }

    private fun validateInput(email: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "נא להזין אימייל"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "אימייל לא תקין"
            return false
        }
        
        return true
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnResetPassword.isEnabled = false
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnResetPassword.isEnabled = true
                    
                    // Check if it's a success message
                    if (state.message.contains("נשלח")) {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnResetPassword.isEnabled = true
                }
                is AuthState.Idle -> {
                    progressBar.visibility = View.GONE
                    btnResetPassword.isEnabled = true
                }
            }
        }
    }
}