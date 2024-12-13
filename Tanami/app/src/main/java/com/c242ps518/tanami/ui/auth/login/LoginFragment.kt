package com.c242ps518.tanami.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.databinding.FragmentLoginBinding
import com.c242ps518.tanami.ui.factory.AuthViewModelFactory
import com.c242ps518.tanami.ui.main.MainActivity

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authPreference: AuthPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(
            requireActivity(),
            AuthViewModelFactory.getInstance()
        )[LoginViewModel::class.java]

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        authPreference = AuthPreference(requireContext())

        binding.loginButton.setOnClickListener {
            val identifier = binding.identifierInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInputs(identifier, password)) {
                loginViewModel.login(identifier, password)
            }
        }

        loginViewModel.loginData.observe(viewLifecycleOwner) { loginData ->
            if (loginData != null) {
                val token = loginData.token
                val name = loginData.name
                val id = loginData.userId

                if (!token.isNullOrEmpty()) {
                    authPreference.saveToken(token)
                    id?.let { authPreference.saveId(it) }
                    name?.let { authPreference.saveName(it) }

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }


        loginViewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.text = ""
                binding.loginButton.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loginButton.text = getString(R.string.login)
                binding.loginButton.isEnabled = true
            }
        }

    }

    private fun validateInputs(identifier: String, password: String): Boolean {
        var isValid = true
        val identifierLayout = binding.identifierInputLayout
        val passwordLayout = binding.passwordInputLayout


        if (identifier.isBlank()) {
            identifierLayout.error = getString(R.string.username_email_cannot_be_empty)
            isValid = false
        } else if (!isValidEmail(identifier) && !isValidUsername(identifier)) {
            identifierLayout.error = getString(R.string.invalid_username_email)
            isValid = false
        } else {
            identifierLayout.error = null
            identifierLayout.isErrorEnabled = false
        }

        if (password.isBlank()) {
            passwordLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 8) {
            passwordLayout.error = getString(R.string.error_short_password)
            isValid = false
        } else {
            passwordLayout.error = null
            passwordLayout.isErrorEnabled = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9._-]{3,}\$".toRegex()
        return usernameRegex.matches(username)
    }
}