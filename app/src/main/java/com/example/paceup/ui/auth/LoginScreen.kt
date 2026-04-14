package com.example.paceup.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.paceup.navigation.Screen
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.AuthState
import com.example.paceup.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    // Navigare automata dupa login reusit
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚔️ PaceUp",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PaceGreen
            )
            Text(
                text = "Run. Level Up. Conquer.",
                fontSize = 16.sp,
                color = PaceGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = PaceGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaceGreen,
                    unfocusedBorderColor = PaceGray,
                    focusedTextColor = PaceWhite,
                    unfocusedTextColor = PaceWhite
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = PaceGray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaceGreen,
                    unfocusedBorderColor = PaceGray,
                    focusedTextColor = PaceWhite,
                    unfocusedTextColor = PaceWhite
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mesaj de eroare
            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaceGreen),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = PaceWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "LOGIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = { navController.navigate(Screen.Register.route) }
            ) {
                Text(
                    text = "Nu ai cont? Înregistrează-te!",
                    color = PaceOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}