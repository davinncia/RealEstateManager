package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes

fun <T: View> Activity.bind(@IdRes res: Int) : Lazy<T> = lazy { findViewById<T>(res) }