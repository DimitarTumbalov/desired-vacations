package com.synergygfs.desiredvacations.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentVacationBinding

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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_vacation, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacation = args.vacation
        updateVacationInfo()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Vacation>("vacation")
            ?.observe(viewLifecycleOwner) {
                vacation = it
                updateVacationInfo()
            }
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
        vacation!!.hotelName?.let { binding.hotelName.text = it }
        vacation!!.necessaryMoneyAmount?.let { binding.necessaryMoneyAmount.text = it.toString() }
        vacation!!.description?.let { binding.description.text = it }
    }
}