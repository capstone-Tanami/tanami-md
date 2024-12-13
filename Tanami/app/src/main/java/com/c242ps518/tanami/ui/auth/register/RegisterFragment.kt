package com.c242ps518.tanami.ui.auth.register

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
import com.c242ps518.tanami.databinding.FragmentRegisterBinding
import com.c242ps518.tanami.ui.factory.AuthViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(
            requireActivity(),
            AuthViewModelFactory.getInstance()
        )[RegisterViewModel::class.java]

        binding.loginLink.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString().trim()

            if (validateInputs(name, username, email, password)) {
                registerViewModel.register(name, username, email, password)
            }
        }

        registerViewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            if (it == "User registered successfully"){
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        registerViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.registerButton.text = ""
                binding.registerButton.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.registerButton.text = getString(R.string.create_account)
                binding.registerButton.isEnabled = true
            }
        }
    }

    private fun validateInputs(name:String, username: String, email: String, password: String): Boolean {
        var isValid = true
        val nameLayout = binding.nameInputLayout
        val usernameLayout = binding.usernameInputLayout
        val emailInput = binding.emailInputLayout
        val passwordLayout = binding.passwordInputLayout

        if (name.isBlank()) {
            nameLayout.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            nameLayout.error = null
            nameLayout.isErrorEnabled = false
        }

        if (username.isBlank()) {
            usernameLayout.error = getString(R.string.error_empty_username)
            isValid = false
        } else if (!isValidUsername(username)) {
            usernameLayout.error = getString(R.string.invalid_username)
            isValid = false
        } else {
            usernameLayout.error = null
            usernameLayout.isErrorEnabled = false
        }

        if (email.isBlank()) {
            emailInput.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = getString(R.string.validationEmail)
            isValid = false
        } else {
            emailInput.error = null
            emailInput.isErrorEnabled = false
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

    private fun isValidUsername(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9._-]{3,}\$".toRegex()
        return usernameRegex.matches(username)
    }
}