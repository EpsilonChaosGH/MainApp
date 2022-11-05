package com.example.mainapp.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mainapp.R
import com.example.mainapp.databinding.FragmentProfileBinding
import com.example.mainapp.utils.findTopNavController


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.editProfileButton.setOnClickListener {
            findTopNavController().navigate(R.id.editProfileFragment)
        }
    }

}