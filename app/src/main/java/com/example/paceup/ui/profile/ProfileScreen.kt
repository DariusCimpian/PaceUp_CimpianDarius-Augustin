package com.example.paceup.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.paceup.navigation.Screen
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.HomeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val user by homeViewModel.user.collectAsState()

    LaunchedEffect(Unit) { homeViewModel.loadUser() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "👤 Profil",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PaceWhite
            )

            // Avatar + nume
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PaceCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(PaceGreen.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏃", fontSize = 40.sp)
                    }

                    Text(
                        text = user?.username ?: "Erou",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaceWhite
                    )
                    Text(
                        text = user?.email ?: "",
                        fontSize = 14.sp,
                        color = PaceGray
                    )

                    Divider(color = PaceGray.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${user?.level ?: 1}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaceYellow
                            )
                            Text(text = "Level", fontSize = 12.sp, color = PaceGray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${user?.xp ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaceGreen
                            )
                            Text(text = "XP", fontSize = 12.sp, color = PaceGray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.3f".format(user?.totalKm ?: 0.0),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaceWhite
                            )
                            Text(text = "km", fontSize = 12.sp, color = PaceGray)
                        }
                    }
                }
            }

            // XP Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaceCardDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Progres spre Level ${(user?.level ?: 1) + 1}",
                        fontSize = 14.sp,
                        color = PaceGray
                    )
                    val xp = user?.xp ?: 0
                    val level = user?.level ?: 1
                    val xpNeeded = level * 500
                    val progress = (xp.toFloat() / xpNeeded).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = PaceGreen,
                        trackColor = PaceDark
                    )
                    Text(
                        text = "$xp / $xpNeeded XP",
                        fontSize = 13.sp,
                        color = PaceGray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            Button(
                onClick = {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "🚪 Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}