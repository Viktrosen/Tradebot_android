package ru.bolotov.core.utils

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class AnalyticManager {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logEvent(event: String, bundle: Bundle = Bundle()) {
        firebaseAnalytics.logEvent(event, bundle)
    }
}