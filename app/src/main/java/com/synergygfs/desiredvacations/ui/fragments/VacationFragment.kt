package com.synergygfs.desiredvacations.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentVacationBinding
import com.synergygfs.desiredvacations.ui.MainActivity

class VacationFragment : Fragment() {

    private lateinit var binding: FragmentVacationBinding

    private val args: VacationFragmentArgs by navArgs()

    private var vacation: Vacation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_vacation, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacationId = args.vacationId

        // Get Vacation from database
        vacation = (activity as MainActivity?)?.dbHelper?.getVacationById(vacationId)

        // Pop BackStack if vacation was deleted
        if (vacation == null)
            findNavController().popBackStack()
        else
            updateVacationInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.vacation_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.edit -> {
                val action =
                    VacationFragmentDirections.actionVacationFragmentToEditVacationFragment(vacation!!)
                findNavController().navigate(action)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateVacationInfo() {
        Glide.with(requireContext())
            .load("${requireContext().cacheDir.path}/vacations_images/${vacation!!.imageName}")
            .centerCrop()
            .placeholder(
                R.drawable.no_image
            )
            .error(R.drawable.default_image)
            .into(binding.image)

        binding.name.text = vacation!!.name
        binding.location.text = vacation!!.location
        binding.date.text = UiUtils.convertDateToString(vacation!!.date)
        binding.remindersLayout.isVisible =
            (activity as MainActivity).reminderManager!!.doRemindersExist(vacation!!)

        if (vacation?.hotelName != null) {
            binding.hotelName.text = vacation?.hotelName
            binding.hotelNameLayout.isVisible = true
        } else
            binding.hotelNameLayout.isVisible = false

        if (vacation?.necessaryMoneyAmount != null) {
            binding.necessaryMoneyAmount.text = vacation?.necessaryMoneyAmount.toString()
            binding.necessaryMoneyAmountLayout.isVisible = true
        } else
            binding.necessaryMoneyAmountLayout.isVisible = false

        if (vacation?.description != null) {
            binding.description.text = vacation?.description
            binding.descriptionLayout.isVisible = true
        } else
            binding.descriptionLayout.isVisible = false
    }
}