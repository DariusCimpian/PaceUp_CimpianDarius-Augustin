package com.example.paceup.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.paceup.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    navController: NavController,
    leaderboardViewModel: LeaderboardViewModel = viewModel()
) {
    val topUsers by leaderboardViewModel.topUsers.collectAsState()
    val topClans by leaderboardViewModel.topClans.collectAsState()
    val isLoading by leaderboardViewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { leaderboardViewModel.loadData() }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "🏆 Clasament",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaceWhite
                    )
                }

                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = PaceCardDark,
                        contentColor = PaceGreen
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Jucători", color = if (selectedTab == 0) PaceGreen else PaceGray) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Clanuri", color = if (selectedTab == 1) PaceGreen else PaceGray) }
                        )
                    }
                }

                if (selectedTab == 0) {
                    itemsIndexed(topUsers) { index, user ->
                        val medal = when (index) {
                            0 -> "🥇"
                            1 -> "🥈"
                            2 -> "🥉"
                            else -> "#${index + 1}"
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index < 3) PaceCardDark else PaceCardDark
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = medal, fontSize = 20.sp)
                                    Column {
                                        Text(
                                            text = user.username,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaceWhite
                                        )
                                        Text(
                                            text = "Level ${user.level} • ${user.totalKm} km",
                                            fontSize = 13.sp,
                                            color = PaceGray
                                        )
                                    }
                                }
                                Text(
                                    text = "${user.xp} XP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaceYellow
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(topClans) { index, clan ->
                        val medal = when (index) {
                            0 -> "🥇"
                            1 -> "🥈"
                            2 -> "🥉"
                            else -> "#${index + 1}"
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = medal, fontSize = 20.sp)
                                    Column {
                                        Text(
                                            text = clan.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaceWhite
                                        )
                                        Text(
                                            text = "👥 ${clan.memberCount} membri",
                                            fontSize = 13.sp,
                                            color = PaceGray
                                        )
                                    }
                                }
                                Text(
                                    text = "${clan.totalXp} XP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaceYellow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}