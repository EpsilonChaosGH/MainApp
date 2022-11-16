package com.example.mainapp.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mainapp.R
import com.example.mainapp.Repositories
import com.example.mainapp.databinding.FragmentDashboardBinding
import com.example.mainapp.utils.viewModelCreator


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var adapter: ElementsAdapter
    private val viewModel by viewModelCreator { DashboardViewModel(Repositories.elementsRepository) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        observeState()
        viewModel.getElements()

        viewModel.elementState.observe(viewLifecycleOwner) {
            adapter.elements = it
        }

        adapter = ElementsAdapter(object : ElementActionListener {
            override fun delete(id: Long) {
                viewModel.deleteElement(id)
            }

            override fun editElementText(id: Long, text: String) {
                editElementButtonPressed(id, text)
            }
        })

        binding.elementsList.adapter = adapter
        binding.elementsList.layoutManager = LinearLayoutManager(requireContext())

        binding.addElementButton.setOnClickListener {
            editElementButtonPressed(-1L, null)
        }

    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.addElementButton.isEnabled = it.enableViews
        binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
    }

    private fun editElementButtonPressed(id: Long, text: String?) {
        val direction =
            DashboardFragmentDirections.actionDashboardFragmentToEditElementFragment(id, text)
        findNavController().navigate(direction)
    }
}