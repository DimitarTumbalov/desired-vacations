package com.synergygfs.desiredvacations.ui.fragments

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentVacationsBinding
import com.synergygfs.desiredvacations.ui.GridLayoutItemDecoration
import com.synergygfs.desiredvacations.ui.MainActivity
import com.synergygfs.desiredvacations.ui.adapters.ItemViewListeners
import com.synergygfs.desiredvacations.ui.adapters.VacationsAdapter
import java.io.File
import java.util.*

class VacationsFragment : Fragment(), ItemViewListeners {

    private lateinit var binding: FragmentVacationsBinding

    var adapter: VacationsAdapter? = null

    private var vacationsCollection = Vector<Vacation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_vacations, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacationsCollection = (activity as MainActivity?)?.dbHelper?.getAllVacations() ?: Vector()

        // Set up the VacationsAdapter
        val vacationsRv = binding.vacationsRv
        adapter = VacationsAdapter(vacationsCollection, this)
        vacationsRv.adapter = adapter

        // Set RecyclerView layout manager
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            changeRecyclerViewLayoutToLinear()
        else
            changeRecyclerViewLayoutToGrid()

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            changeRecyclerViewLayoutToLinear()
        else
            changeRecyclerViewLayoutToGrid()
    }

    private fun changeRecyclerViewLayoutToLinear() {
        binding.vacationsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())

            if (itemDecorationCount > 0)
                removeItemDecorationAt(0)

            addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun changeRecyclerViewLayoutToGrid() {
        binding.vacationsRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)

            if (itemDecorationCount > 0)
                removeItemDecorationAt(0)

            addItemDecoration(
                GridLayoutItemDecoration(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        1f,
                        context.resources.displayMetrics
                    ).toInt()
                )
            )
        }
    }

    override fun onClick(vacationId: Int) {
        val action =
            VacationsFragmentDirections.actionVacationsFragmentToVacationFragment(vacationId)
        findNavController().navigate(action)
    }

    override fun onLongClick(vacation: Vacation) {
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

                    // Delete the previous image
                    val file =
                        File("${activity.cacheDir.path}/vacations_images/${vacation.imageName}")
                    if (file.exists())
                        file.delete()

                    val deletedRow = activity.dbHelper?.deleteVacationById(vacation.id)

                    if (deletedRow != null && deletedRow > -1) {
                        // Cancel previous reminders
                        activity.reminderManager?.cancelReminders(vacation)

                        val vacationToDeleteIndex =
                            vacationsCollection.indexOf(vacationsCollection.find { it.id == vacation.id })
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