package com.example.gesturerecognitionwebchat.context

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.example.gesturerecognitionwebchat.R
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.android.synthetic.main.fragment_alert_dialog_beta.view.*

fun Context.alertWithActions(
    message: String,
    cancelable: Boolean = true,
    positiveButtonCallback: () -> Unit,
    negativeButtonCallback: () -> Unit,
    positiveText: String,
    negativeText: String
) {
    val inflater: LayoutInflater = this.layoutInflater
    val dialogView: View = inflater.inflate(R.layout.fragment_alert_dialog_beta, null)

    val dialog = AlertDialog.Builder(this)
        .setView(dialogView)
        .setCancelable(cancelable)
        .create()

    dialogView.agreeAlert.text = positiveText
    dialogView.cancelAlert.text = negativeText
    dialogView.messageAlert.text = message
    dialogView.agreeAlert.setOnClickListener {
        positiveButtonCallback()
        dialog.dismiss()
    }
    dialogView.cancelAlert.setOnClickListener {
        negativeButtonCallback()
        dialog.dismiss()
    }
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
}

fun Context.showUpperToast(message: String) {
    val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
    val inflater: LayoutInflater = this.layoutInflater
    val dialogView: View = inflater.inflate(R.layout.custom_toast, null)
    dialogView.toast_text.text = message
    dialogView
    toast.view = dialogView
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
    Handler(Looper.getMainLooper()).postDelayed({
        toast.cancel()
    }, 1000)
}

fun Context.showUpperToastError(message: String) {
    val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
    val inflater: LayoutInflater = this.layoutInflater
    val dialogView: View = inflater.inflate(R.layout.custom_toast_error, null)
    dialogView.toast_text.text = message
    dialogView
    toast.view = dialogView
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
    Handler(Looper.getMainLooper()).postDelayed({
        toast.cancel()
    }, 1000)
}

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return context.layoutInflater.inflate(layoutRes, this, attachToRoot)
}

internal val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)