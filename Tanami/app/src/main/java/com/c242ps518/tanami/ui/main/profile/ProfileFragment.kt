package com.c242ps518.tanami.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.DataProfile
import com.c242ps518.tanami.databinding.FragmentProfileBinding
import com.c242ps518.tanami.ui.auth.AuthActivity
import com.c242ps518.tanami.ui.factory.ProfileViewModelFactory
import com.c242ps518.tanami.ui.main.profile.editprofile.EditProfileActivity
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(
            requireActivity(),
            ProfileViewModelFactory.getInstance(requireContext())
        )[ProfileViewModel::class.java]

        binding.layoutLogout.setOnClickListener {
            val logoutBottomSheet = LogoutBottomSheet.newInstance {
                logout()
            }
            logoutBottomSheet.show(requireActivity().supportFragmentManager, LogoutBottomSheet.TAG)
        }

        binding.layoutLanguage.setOnClickListener {
            val languageBottomSheet = LanguageBottomSheet()
            languageBottomSheet.setOnLanguageSelectedListener { selectedLanguage ->
                profileViewModel.saveLanguage(selectedLanguage)
                setAppLocale(requireContext(), selectedLanguage)
                requireActivity().recreate()
            }

            languageBottomSheet.show(
                requireActivity().supportFragmentManager,
                LanguageBottomSheet.TAG
            )
        }

        binding.layoutProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        profileViewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                setProfile(profile)
            }
        }

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProfile(profile: DataProfile) {
        binding.tvProfileName.text = profile.name
        binding.tvProfileEmail.text = profile.email
        binding.tvUsername.text = profile.username
        Glide.with(binding.ivProfilePicture.context)
            .load(profile.profilePicture)
            .placeholder(R.drawable.baseline_account_circle_24)
            .into(binding.ivProfilePicture)
    }

    private fun logout() {
        profileViewModel.clearAuthData()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}