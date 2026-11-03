package br.com.easyreminder.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(getString(messageRes), duration)
}

fun Context.getColorCompat(@ColorRes colorRes: Int): Int =
    ContextCompat.getColor(this, colorRes)

fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableRes)