package ru.bolotov.core.uikit.components

import android.content.Context
import android.widget.Toast
import androidx.annotation.IntDef

@IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
@Retention(AnnotationRetention.SOURCE)
annotation class ToastDuration

fun Context.toast(message: String, @ToastDuration duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}