package com.example.paceup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.paceup.navigation.NavGraph
import com.example.paceup.ui.theme.PaceUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaceUpTheme {
                NavGraph()
            }
        }
    }
}