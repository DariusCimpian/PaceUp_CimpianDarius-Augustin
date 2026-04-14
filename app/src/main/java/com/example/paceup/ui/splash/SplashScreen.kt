package com.example.paceup.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.paceup.navigation.Screen
import com.example.paceup.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        delay(1500)
        if (auth.currentUser != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaceDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚔️",
                fontSize = 80.sp
            )
            Text(
                text = "PaceUp",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PaceGreen
            )
            Text(
                text = "Run. Level Up. Conquer.",
                fontSize = 16.sp,
                color = PaceGray
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = PaceGreen)
        }
    }
}