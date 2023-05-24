package com.example.gesturerecognitionwebchat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gesturerecognitionwebchat.databinding.ActivityChangeBinding

class ChangeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChangeBinding
    private var backPressCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("test2323", "Here")

        binding = ActivityChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_change)
        binding.root.post {
            when (intent.getIntExtra("changeData", 0)) {
                1 -> {
                    Log.d("test2323", "Here1")
                    navController.navigate(R.id.ChangePassword)
                }
                2 -> {
                    Log.d("test2323", "Here2")
                    navController.navigate(R.id.ChangeEmail)
                }
                else -> {
                    // Default navigation if no valid "changeData" extra is received
                    navController.navigate(R.id.ChangePassword)
                }
            }
        }
    }

    override fun onBackPressed() {
        backPressCount++
        super.onBackPressed()
        if (backPressCount <= 2) {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_change)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}