package company.tap.tapcardsdk.internal.logic.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager



/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {


    /**
     * Hide keyboard from.
     *
     * @param context the context
     * @param view    the view
     */
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getDeviceDisplayMetrics(activity: Activity) : Int{
        // Determine density
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val density = metrics.densityDpi
        return density
    }
}