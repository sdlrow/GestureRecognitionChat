package com.example.gesturerecognitionwebchat.Launcher

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.gesturerecognitionwebchat.R
import com.example.gesturerecognitionwebchat.databinding.ActivityLauncherBinding
import com.scottyab.rootbeer.RootBeer

class LauncherActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (isDeviceRooted()) {
//            showAlertDialog()
//        }
    }

    fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Ваше устройство взломано")
            .setMessage("Пользоваться данным приложением запрещено")
            .setPositiveButton("Закрыть приложение") { _, _ ->
                this@LauncherActivity.finish()
            }
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,
                R.color.white
            ))
        }
        dialog.show()
    }

    private fun isDeviceRooted(): Boolean {
        val rootBeer = RootBeer(this)
        return rootBeer.isRootedWithoutBusyBoxCheck
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_launcher)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}