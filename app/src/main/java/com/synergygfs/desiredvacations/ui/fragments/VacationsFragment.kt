package com.synergygfs.desiredvacations.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentVacationsBinding
import com.synergygfs.desiredvacations.ui.MainActivity
import com.synergygfs.desiredvacations.ui.adapters.VacationsAdapter
import java.util.*

class VacationsFragment : Fragment() {

    private lateinit var binding: FragmentVacationsBinding

    var adapter: VacationsAdapter? = null

    var vacationsCollection = Vector<Vacation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_vacations, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacationsCollection = (activity as MainActivity?)?.dbHelper?.getAllVacations() ?: Vector()

        // Set up the CitiesAdapter
        val citiesRv = binding.vacationsRv
        val lm = LinearLayoutManager(requireContext())
        citiesRv.layoutManager = lm
        adapter = VacationsAdapter(vacationsCollection)
        citiesRv.adapter = adapter

        // Add dividers between RecyclerView items
        val dividerItemDecoration = DividerItemDecoration(
            citiesRv.context,
            lm.orientation
        )
        citiesRv.addItemDecoration(dividerItemDecoration)

        // Update the RecyclerView UI
        binding.noVacationsScreen.isVisible = adapter?.itemCount ?: 0 < 1

        // Register an observer to the adapter so it can update the RecyclerView UI
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)

                binding.noVacationsScreen.isVisible = adapter?.itemCount ?: 0 < 1
            }

            override fun onChanged() {
                super.onChanged()

                binding.noVacationsScreen.isVisible = adapter?.itemCount ?: 0 < 1
            }
        })

        binding.addVacationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_vacationsFragment_to_addVacationFragment)
        }
    }

    companion object {
        const val TAG = "VacationsFragment"
    }
}