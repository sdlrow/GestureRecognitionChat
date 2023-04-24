package com.example.gesturerecognitionwebchat.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.gesturerecognitionwebchat.utils.EventObserver
import com.example.gesturerecognitionwebchat.utils.Status
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }

    open fun showProgress() {
        // do nothing
    }

    open fun hideProgress() {
        // do nothing
    }

    open fun success() {
        // do nothing
    }

    open fun onError(message: String) {
        // do nothing
    }

    protected open val statusObserver = Observer<Status> {
        it?.let {
            Log.d("test23Stat" , it.toString())
            when (it) {
                Status.SHOW_LOADING -> showProgress()
                Status.HIDE_LOADING -> hideProgress()
                Status.SUCCESS -> success()
            }
        }
    }

    protected open val errorMessageObserver = EventObserver<String> {
        Log.d("test23Err" , it.toString())
        onError(it)
    }
}
