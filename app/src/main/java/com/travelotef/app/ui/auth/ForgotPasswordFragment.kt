package com.travelotef.app.ui.auth

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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.travelotef.app.R
import com.travelotef.app.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail: TextInputEditText = view.findViewById(R.id.etEmail)
        val btnReset: MaterialButton = view.findViewById(R.id.btnResetPassword)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        btnReset.setOnClickListener {
            viewModel.resetPassword(etEmail.text.toString().trim())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnReset.isEnabled = false
                        }
                        is UiState.Success -> {
                            progressBar.visibility = View.GONE
                            btnReset.isEnabled = true
                            Toast.makeText(context, "מייל לאיפוס סיסמה נשלח בהצלחה", Toast.LENGTH_LONG).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                        is UiState.Error -> {
                            progressBar.visibility = View.GONE
                            btnReset.isEnabled = true
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            progressBar.visibility = View.GONE
                            btnReset.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}
