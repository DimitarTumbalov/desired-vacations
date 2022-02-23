package com.synergygfs.desiredvacations.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentVacationsBinding
import com.synergygfs.desiredvacations.ui.MainActivity
import com.synergygfs.desiredvacations.ui.adapters.ItemViewListeners
import com.synergygfs.desiredvacations.ui.adapters.VacationsAdapter
import java.util.*

class VacationsFragment : Fragment(), ItemViewListeners {

    private lateinit var binding: FragmentVacationsBinding

    var adapter: VacationsAdapter? = null

    private var vacationsCollection = Vector<Vacation>()

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
        adapter = VacationsAdapter(vacationsCollection, this)
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

    override fun onClick(vacation: Vacation) {
        val action = VacationsFragmentDirections.actionVacationsFragmentToVacationFragment(vacation)
        findNavController().navigate(action)
    }

    override fun onLongClick(vacationId: Int) {
        (activity as MainActivity?)?.let { activity ->
            val dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.dialog_confirm_action)

            val body = dialog.findViewById<TextView>(R.id.body)
            val confirmBtn = dialog.findViewById<MaterialButton>(R.id.confirm_btn)
            val cancelBtn = dialog.findViewById<MaterialButton>(R.id.cancel_btn)

            // set body text
            body.text = getString(R.string.vacation_delete_confirm_msg)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            confirmBtn.apply {
                text = getString(R.string.delete)

                // set confirm btn click listener
                setOnClickListener {
                    dialog.dismiss()

                    val deletedRow = activity.dbHelper?.deleteVacationById(vacationId)

                    if (deletedRow != null && deletedRow > -1) {
                        val vacationToDeleteIndex =
                            vacationsCollection.indexOf(vacationsCollection.find { it.id == vacationId })
                        vacationsCollection.removeAt(vacationToDeleteIndex)
                        adapter?.notifyItemRemoved(vacationToDeleteIndex)

                        Toast.makeText(
                            activity,
                            getString(R.string.vacation_delete_success_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            getString(R.string.vacation_delete_fail_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                dialog.setCancelable(true)

                activity.showDialog(dialog)

                val window: Window? = dialog.window
                window?.setLayout(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                window?.setBackgroundDrawableResource(R.color.transparent)
            }
        }
    }
}