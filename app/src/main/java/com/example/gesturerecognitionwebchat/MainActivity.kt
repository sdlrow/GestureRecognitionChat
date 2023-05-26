package com.example.gesturerecognitionwebchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gesturerecognitionwebchat.context.MessageType
import com.example.gesturerecognitionwebchat.context.alertWithActions
import com.example.gesturerecognitionwebchat.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_start_chat,
                R.id.nav_chats,
                R.id.nav_notifications,
                R.id.nav_stat,
                R.id.nav_account,
                R.id.nav_policy_conf,
                R.id.nav_policy_agr
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.toString()) {
            ASK -> {
                Log.d("test2322", item.toString())
                this.alertWithActions(
                    message = "Приложение разработано студентами МУИТА: \n " +
                            "Калжигитов Нурбол,\n Ли Александ,\n Жексенов Даниял,\n Евдокимов Даниил \n" +
                            "Научный руководитель:\n Самат Бахытжанович",
                    positiveButtonCallback = {},
                    negativeButtonCallback = {},
                    negativeText = "Ок",
                    type = MessageType.NO_ACTION
                )
            }
            else -> {
                Log.d("test2321", item.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAskMenuItemClick() {
        Log.d("test232", "12")
    }

    override fun onBackPressed() {
        alertWithActions(
            message = "\n Вы точно хотите выйти? \n",
            positiveButtonCallback = {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivityForResult(intent, 0)
            },
            negativeButtonCallback = {},
            positiveText = "Выйти", negativeText = "Отменить"
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        const val ASK = "Ask"
    }
}