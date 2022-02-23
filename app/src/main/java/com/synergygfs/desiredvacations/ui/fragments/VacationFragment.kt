package com.synergygfs.desiredvacations.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.databinding.FragmentVacationBinding

class VacationFragment : Fragment() {

    private lateinit var binding: FragmentVacationBinding

    private val args: VacationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_vacation, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacation = args.vacation

        val date = binding.date
        val name = binding.name
        val location = binding.location
        val hotelName = binding.hotelName
        val necessaryMoneyAmount = binding.necessaryMoneyAmount
        val description = binding.description

        Glide.with(requireContext())
            .load("${requireContext().cacheDir.path}/vacations_images/${vacation.imageName}")
            .centerCrop()
            .placeholder(
                R.drawable.no_image
            )
            .error(R.drawable.default_image)
            .into(binding.image)

        name.text = vacation.name
        location.text = vacation.location
        date.text = UiUtils.convertDateToString(vacation.date)
        vacation.hotelName?.let { hotelName.text = it }
        vacation.necessaryMoneyAmount?.let { necessaryMoneyAmount.text = it.toString() }
        vacation.description?.let { description.text = it }
    }
}