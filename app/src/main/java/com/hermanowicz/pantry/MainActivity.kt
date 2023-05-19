package com.hermanowicz.pantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.hermanowicz.pantry.navigation.features.AppNavHost
import com.hermanowicz.pantry.ui.theme.MyPantryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        analytics = Firebase.analytics
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        setContent {
            MyPantryTheme {
                AppNavHost()
            }
        }
    }
}
