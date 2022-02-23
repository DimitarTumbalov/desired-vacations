@file:Suppress("unused")

package com.synergygfs.desiredvacations.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.UiUtils
import com.synergygfs.desiredvacations.data.VacationsContract.VacationEntity
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.FragmentEditVacationBinding
import com.synergygfs.desiredvacations.ui.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class EditVacationFragment : Fragment() {

    private lateinit var binding: FragmentEditVacationBinding

    private val args: VacationFragmentArgs by navArgs()

    private lateinit var vacation: Vacation

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

                loadImage()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_vacation, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacation = args.vacation

        val name = binding.name
        val location = binding.location
        val date = binding.date

        name.doOnTextChanged { text, _, _, _ ->
            isNameValid = !text.isNullOrBlank()

            validateForm()
        }

        location.doOnTextChanged { text, _, _, _ ->
            isLocationValid = !text.isNullOrBlank()

            validateForm()
        }

        date.setOnClickListener {
            pickDate()
        }

        binding.chooseImageBtn.setOnClickListener {
            pickImage()
        }

        binding.imageOptionsBtn.setOnClickListener {
            showMenu(it, R.menu.image_popup_menu)
        }

        binding.saveBtn.setOnClickListener {
            updateVacation()
        }

        imageBmp =
            BitmapFactory.decodeFile("${requireContext().cacheDir}/vacations_images/${vacation.imageName}")

        loadImage()
        name.setText(vacation.name)
        location.setText(vacation.location)
        setDate(vacation.date)
        vacation.hotelName?.let { binding.hotelName.setText(it) }
        vacation.necessaryMoneyAmount?.let { binding.necessaryMoneyAmount.setText(it.toString()) }
        vacation.description?.let { binding.description.setText(it) }
    }

    private fun validateForm() {
        binding.saveBtn.isEnabled = isNameValid && isLocationValid && date != null
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
                            binding.date.setText(UiUtils.convertDateToString(it))
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

    private fun loadImage() {
        Glide.with(this)
            .load(imageBmp).centerCrop()
            .placeholder(R.drawable.no_image)
            .apply(
                RequestOptions()
                    .error(R.drawable.no_image)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageOptionsBtn.isVisible = false
                    binding.chooseImageBtn.isVisible = true

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.chooseImageBtn.isVisible = false
                    binding.imageOptionsBtn.isVisible = true

                    return false
                }

            }).into(binding.image)
    }

    private fun removeImage() {
        binding.imageOptionsBtn.isVisible = false
        imageBmp = null
        Glide.with(this).load(R.drawable.no_image).placeholder(R.drawable.no_image)
            .into(binding.image)
        binding.chooseImageBtn.isVisible = true
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.change_image -> {
                    pickImage()
                }
                R.id.remove_image -> {
                    removeImage()
                }
            }

            true
        }

        // Show the popup menu.
        popup.show()
    }

    private fun setDate(date: Date) {
        this.date = date
        binding.date.setText(UiUtils.convertDateToString(date))
        validateForm()
    }

    private fun updateVacation() {
        val name = binding.name.text.toString()
        val location = binding.location.text.toString()
        val date = binding.date.text.toString()
        val hotelName = binding.hotelName.text.toString()
        val necessaryMoneyAmount = binding.necessaryMoneyAmount.text.toString().toInt()
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
            put(BaseColumns._ID, vacation.id)
            put(VacationEntity.COLUMN_NAME_NAME, name)
            put(VacationEntity.COLUMN_NAME_LOCATION, location)
            put(VacationEntity.COLUMN_NAME_DATE, date)
            put(VacationEntity.COLUMN_NAME_HOTEL_NAME, hotelName)
            put(VacationEntity.COLUMN_NAME_NECESSARY_MONEY_AMOUNT, necessaryMoneyAmount)
            put(VacationEntity.COLUMN_NAME_DESCRIPTION, description)
            put(VacationEntity.COLUMN_NAME_IMAGE_NAME, imageName)
        }

        // Insert the new row, returning the primary key value of the new row
        (activity as MainActivity?)?.let { activity ->

            // Delete the previous image
            val file = File("${activity.cacheDir.path}/vacations_images/${vacation.imageName}")
            if (file.exists())
                file.delete()

            val newRowId = activity.dbHelper?.updateVacation(
                values
            )

            if (newRowId != null && newRowId > -1) {
                Toast.makeText(
                    activity,
                    getString(R.string.vacation_edit_success),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().apply {
                    previousBackStackEntry?.savedStateHandle?.set(
                        "vacation", Vacation(
                            vacation.id,
                            name,
                            location,
                            this@EditVacationFragment.date!!,
                            hotelName,
                            necessaryMoneyAmount,
                            description,
                            imageName
                        )
                    )
                    popBackStack()
                }
            } else // Show a toast that vacation creation failed
                Toast.makeText(
                    activity,
                    getString(R.string.vacation_edit_fail),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}