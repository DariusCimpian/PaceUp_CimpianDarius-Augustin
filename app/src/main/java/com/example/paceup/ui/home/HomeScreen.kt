package com.example.paceup.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val user by homeViewModel.user.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.loadUser()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaceDark)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PaceGreen
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Salut, ${user?.username ?: "Erou"}! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceWhite
                )
                Text(
                    text = "Level ${user?.level ?: 1} Runner",
                    fontSize = 14.sp,
                    color = PaceGreen
                )

                // Card personaj + XP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🏃", fontSize = 64.sp)

                        Text(
                            text = "Level ${user?.level ?: 1}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaceYellow
                        )

                        val xp = user?.xp ?: 0
                        val xpNeeded = homeViewModel.xpForNextLevel(user?.level ?: 1)
                        val progress = (xp.toFloat() / xpNeeded).coerceIn(0f, 1f)

                        Text(
                            text = "$xp / $xpNeeded XP",
                            fontSize = 14.sp,
                            color = PaceGray
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp),
                            color = PaceGreen,
                            trackColor = PaceCardDark
                        )
                    }
                }

                // Statistici rapide
                Text(
                    text = "Statistici",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceWhite
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🏅",
                        label = "Total km",
                        value = "%.3f".format(user?.totalKm ?: 0.0)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "⚡",
                        label = "XP Total",
                        value = "${user?.xp ?: 0}"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🎯",
                        label = "Level",
                        value = "${user?.level ?: 1}"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "⚔️",
                        label = "Clan",
                        value = if ((user?.clanId ?: "").isEmpty()) "Fără clan" else "În clan"
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaceCardDark)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 28.sp)
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PaceWhite
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = PaceGray
            )
        }
    }
}