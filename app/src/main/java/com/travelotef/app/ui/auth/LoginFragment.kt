package com.travelotef.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.travelotef.app.MainActivity
import com.travelotef.app.R
import com.travelotef.app.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail: TextInputEditText = view.findViewById(R.id.etEmail)
        val etPassword: TextInputEditText = view.findViewById(R.id.etPassword)
        val btnLogin: MaterialButton = view.findViewById(R.id.btnLogin)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            viewModel.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString()
            )
        }

        // Navigate to register
        view.findViewById<View>(R.id.tvRegister)?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        // Navigate to forgot password
        view.findViewById<View>(R.id.tvForgotPassword)?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnLogin.isEnabled = false
                        }
                        is UiState.Success -> {
                            progressBar.visibility = View.GONE
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                        is UiState.Error -> {
                            progressBar.visibility = View.GONE
                            btnLogin.isEnabled = true
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            progressBar.visibility = View.GONE
                            btnLogin.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}
