package com.synergygfs.desiredvacations.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.synergygfs.desiredvacations.AlarmManager
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.DbHelper
import com.synergygfs.desiredvacations.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var dbHelper: DbHelper? = null
    var alarmManager: AlarmManager? = null

    private var lastDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val toolbar = binding.toolbar

        // Set up the toolbar
        setSupportActionBar(toolbar)

        // Set up the toolbar with NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Set up DbHelper
        dbHelper = DbHelper(this)

        // Set up AlarmManager
        alarmManager = AlarmManager(this)
    }

    fun showDialog(dialog: Dialog) {
        if (lastDialog?.isShowing == true)
            lastDialog?.dismiss()

        dialog.show()
        lastDialog = dialog
    }

}