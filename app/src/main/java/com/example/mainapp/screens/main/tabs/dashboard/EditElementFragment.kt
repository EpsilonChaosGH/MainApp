package com.example.mainapp.screens.main.tabs.dashboard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mainapp.R
import com.example.mainapp.Repositories
import com.example.mainapp.databinding.FragmentEditElementBinding
import com.example.mainapp.utils.observeEvent
import com.example.mainapp.utils.viewModelCreator

class EditElementFragment : Fragment(R.layout.fragment_edit_element) {

    private val viewModel by viewModelCreator { EditElementViewModel(Repositories.elementsRepository) }
    private lateinit var binding: FragmentEditElementBinding
    private val args by navArgs<EditElementFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditElementBinding.bind(view)

        if (savedInstanceState == null && args.elementText != null) {
            binding.elementEditText.setText(args.elementText)
        }

        binding.saveButton.setOnClickListener {
            viewModel.editElementSaveButtonPressed(args.elementId, binding.elementEditText.text.toString())
        }
        binding.cancelButton.setOnClickListener {
            hideKeyboardFrom(binding.elementEditText)
            findNavController().popBackStack()
        }


        observeState()
        observeGoBackEvent()
        showSoftKeyboard(binding.elementEditText)
    }


    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
    private fun hideKeyboardFrom(view: View?) {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.elementEditText.error =
            if (it.emptyTextError) getString(R.string.field_is_empty) else null

        binding.elementEditText.isEnabled = it.enableViews
        binding.saveButton.isEnabled = it.enableViews
        binding.cancelButton.isEnabled = it.enableViews
        binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
}