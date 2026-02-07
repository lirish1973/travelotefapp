package com.example.travelotefapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.travelotefapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: SignInButton
    private lateinit var tvRegister: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    viewModel.signInWithGoogle(it)
                }
            } catch (e:  ApiException) {
                Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupGoogleSignIn()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn)
        tvRegister = view.findViewById(R.id.tvRegister)
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (validateInput(email, password)) {
                viewModel.signInWithEmail(email, password)
            }
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        tvRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        tvForgotPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, ForgotPasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun validateInput(email:  String, password: String): Boolean {
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
        
        return true
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                    btnGoogleSignIn.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "התחברות הצליחה!  🎉", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    btnGoogleSignIn.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is AuthState.Idle -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    btnGoogleSignIn.isEnabled = true
                }
            }
        }
    }
}