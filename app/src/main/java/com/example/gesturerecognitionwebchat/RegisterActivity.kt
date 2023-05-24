package com.example.gesturerecognitionwebchat

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gesturerecognitionwebchat.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.settingsIcon.setOnClickListener {
//
//        }
    }
    override fun onBackPressed() {
        // Do nothing to disable the back button press
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_register)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}