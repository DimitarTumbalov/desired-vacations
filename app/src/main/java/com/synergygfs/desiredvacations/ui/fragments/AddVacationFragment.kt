package com.synergygfs.desiredvacations.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.data.VacationsContract.VacationEntity
import com.synergygfs.desiredvacations.databinding.FragmentAddVacationBinding
import com.synergygfs.desiredvacations.ui.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class AddVacationFragment : Fragment() {

    private lateinit var binding: FragmentAddVacationBinding

    private var isNameValid = false
    private var isLocationValid = false

    private var imageBmp: Bitmap? = null
    private var date: Date? = null

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null)
                Toast.makeText(requireContext(), "Image couldn't be resolved", Toast.LENGTH_SHORT)
            else {
                val inputStream =
                    requireContext().contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(inputStream)
                imageBmp = ThumbnailUtils.extractThumbnail(bmp, 640, 360)

                Glide.with(this).load(imageBmp).centerCrop().into(binding.image)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_vacation, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseDateBtn.setOnClickListener {
            pickDate()
        }

        binding.chooseImageBtn.setOnClickListener {
            pickImage()
        }

        binding.addBtn.setOnClickListener {
            addVacation()
        }

        binding.name.doOnTextChanged { text, _, _, _ ->
            isNameValid = !text.isNullOrBlank()

            validateForm()
        }

        binding.location.doOnTextChanged { text, _, _, _ ->
            isLocationValid = !text.isNullOrBlank()

            validateForm()
        }
    }

    private fun validateForm() {
        binding.addBtn.isEnabled = isNameValid && isLocationValid && date != null
    }

    private fun pickDate() {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Show time picker dialog
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->

                        // Create picked date
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)

                        // Set the date
                        pickedDateTime.time.let {
                            this.date = it
                            binding.date.text = UiUtils.formatDate(it)
                            validateForm()
                        }
                    },
                    currentHour,
                    currentMinute,
                    DateFormat.is24HourFormat(requireContext())
                ).show()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.datePicker.minDate = now
        // Show date picker dialog
        datePickerDialog.show()
    }

    private fun pickImage() {
        selectImageFromGalleryResult.launch("image/*")
    }

    private fun addVacation() {
        val name = binding.name.text.toString()
        val hotelName = binding.hotelName.text.toString()
        val local = binding.location.text.toString()
        val date = binding.date.text.toString()
        val necessaryMoneyAmount = binding.necessaryMoneyAmount.text.toString()
        val description = binding.description.text.toString()
        var imageName: String? = null

        if (imageBmp != null) {
            imageName = "img${System.currentTimeMillis()}"

            val dir = File(requireActivity().cacheDir, "vacations_images")
            val file = File(dir, imageName)

            if (!dir.exists())
                dir.mkdirs()

            if (file.exists())
                file.delete()

            file.createNewFile()
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBmp?.compress(Bitmap.CompressFormat.PNG, 92, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            file.writeBytes(byteArray)
        }

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(VacationEntity.COLUMN_NAME_NAME, name)
            put(VacationEntity.COLUMN_NAME_HOTEL_NAME, hotelName)
            put(VacationEntity.COLUMN_NAME_LOCATION, local)
            put(VacationEntity.COLUMN_NAME_DATE, date)
            put(VacationEntity.COLUMN_NAME_NECESSARY_MONEY_AMOUNT, necessaryMoneyAmount)
            put(VacationEntity.COLUMN_NAME_DESCRIPTION, description)
            put(VacationEntity.COLUMN_NAME_IMAGE_NAME, imageName)
        }

        // Insert the new row, returning the primary key value of the new row
        (activity as MainActivity?)?.let { activity ->
            val newRowId = activity.dbHelper?.insert(
                VacationEntity.TABLE_NAME,
                values
            )

            if (newRowId != null && newRowId > -1) {
                Toast.makeText(
                    activity,
                    getString(R.string.vacation_add_success),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()
            } else // Show a toast that city creation failed
                Toast.makeText(
                    activity,
                    getString(R.string.vacation_add_fail),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    companion object {
        const val TAG = "AddVacationFragment"
    }
}