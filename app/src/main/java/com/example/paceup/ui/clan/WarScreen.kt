package com.example.paceup.ui.clan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paceup.model.Clan
import com.example.paceup.model.ClanWar
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.WarViewModel
import java.util.concurrent.TimeUnit

@Composable
fun WarScreen(
    userClan: Clan,
    allClans: List<Clan>,
    userRole: String,
    warViewModel: WarViewModel = viewModel()
) {
    val activeWar by warViewModel.activeWar.collectAsState()
    val pendingWars by warViewModel.pendingWars.collectAsState()
    val message by warViewModel.message.collectAsState()
    val isLoading by warViewModel.isLoading.collectAsState()

    var showChallengeDialog by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(7) }
    var selectedTargetClan by remember { mutableStateOf<Clan?>(null) }
    var declineReason by remember { mutableStateOf("") }
    var showDeclineDialog by remember { mutableStateOf(false) }
    var warToDecline by remember { mutableStateOf<ClanWar?>(null) }

    LaunchedEffect(userClan.id) {
        warViewModel.loadWars(userClan.id)
    }

    if (showChallengeDialog && selectedTargetClan != null) {
        AlertDialog(
            onDismissRequest = { showChallengeDialog = false },
            containerColor = PaceCardDark,
            title = {
                Text(
                    "Provoacă ${selectedTargetClan!!.name}",
                    color = PaceWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Alege durata battle-ului:", color = PaceGray, fontSize = 14.sp)
                    listOf(1, 3, 7).forEach { days ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedDays == days,
                                onClick = { selectedDays = days },
                                colors = RadioButtonDefaults.colors(selectedColor = PaceGreen)
                            )
                            Column {
                                Text(
                                    text = when (days) {
                                        1 -> "1 zi"
                                        3 -> "3 zile"
                                        else -> "7 zile (Standard)"
                                    },
                                    color = PaceWhite,
                                    fontSize = 14.sp,
                                    fontWeight = if (days == 7) FontWeight.Bold else FontWeight.Normal
                                )
                                if (days == 7) {
                                    Text("Recomandat", fontSize = 11.sp, color = PaceGreen)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        warViewModel.sendChallenge(
                            userClan.id, userClan.name,
                            selectedTargetClan!!.id, selectedTargetClan!!.name,
                            selectedDays
                        )
                        showChallengeDialog = false
                        selectedTargetClan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PaceOrange)
                ) {
                    Text("⚔️ Provoacă!", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showChallengeDialog = false
                    selectedTargetClan = null
                }) {
                    Text("Anulează", color = PaceGray)
                }
            }
        )
    }

    if (showDeclineDialog && warToDecline != null) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            containerColor = PaceCardDark,
            title = {
                Text("Refuzi provocarea?", color = PaceWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Refuzi provocarea de la ${warToDecline!!.challengerClanName}?",
                        color = PaceGray,
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = declineReason,
                        onValueChange = { declineReason = it },
                        label = { Text("Motiv (opțional)", color = PaceGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaceGreen,
                            unfocusedBorderColor = PaceGray,
                            focusedTextColor = PaceWhite,
                            unfocusedTextColor = PaceWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        warViewModel.respondToWar(warToDecline!!.id, false, declineReason)
                        showDeclineDialog = false
                        warToDecline = null
                        declineReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Refuz") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeclineDialog = false
                    warToDecline = null
                }) {
                    Text("Anulează", color = PaceGray)
                }
            }
        )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "⚔️ Clan War",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaceWhite
                    )
                }

                message?.let { msg ->
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.startsWith("✅") || msg.startsWith("⚔️"))
                                    PaceGreen.copy(alpha = 0.15f)
                                else PaceOrange.copy(alpha = 0.15f)
                            )
                        ) {
                            Text(
                                msg,
                                modifier = Modifier.padding(12.dp),
                                color = if (msg.startsWith("✅") || msg.startsWith("⚔️")) PaceGreen else PaceOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // War activ
                item {
                    if (activeWar != null) {
                        val war = activeWar!!
                        val remaining = war.endTime - System.currentTimeMillis()
                        val daysLeft = TimeUnit.MILLISECONDS.toDays(remaining).coerceAtLeast(0)
                        val hoursLeft = (TimeUnit.MILLISECONDS.toHours(remaining) % 24).coerceAtLeast(0)
                        val myXp = if (war.challengerClanId == userClan.id) war.challengerXp else war.challengedXp
                        val enemyXp = if (war.challengerClanId == userClan.id) war.challengedXp else war.challengerXp
                        val enemyName = if (war.challengerClanId == userClan.id) war.challengedClanName else war.challengerClanName
                        val total = (myXp + enemyXp).toFloat().coerceAtLeast(1f)

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(PaceOrange.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("WAR ACTIV", fontSize = 11.sp, color = PaceOrange, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        "$daysLeft z $hoursLeft h rămas",
                                        fontSize = 12.sp,
                                        color = PaceGray
                                    )
                                }

                                Text(
                                    "${userClan.name} vs $enemyName",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaceWhite
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "$myXp XP",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaceGreen
                                    )
                                    Text("vs", fontSize = 14.sp, color = PaceGray)
                                    Text(
                                        "$enemyXp XP",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaceOrange
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { myXp / total },
                                    modifier = Modifier.fillMaxWidth().height(10.dp),
                                    color = PaceGreen,
                                    trackColor = PaceOrange
                                )

                                Text(
                                    when {
                                        myXp > enemyXp -> "🏆 Câștigați momentan!"
                                        myXp == enemyXp -> "🤝 Egalitate!"
                                        else -> "💪 Mai alergați, recuperați!"
                                    },
                                    color = if (myXp >= enemyXp) PaceGreen else PaceOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Niciun war activ", fontSize = 16.sp, color = PaceGray, fontWeight = FontWeight.Bold)
                                Text("Provoacă un clan sau așteaptă o provocare!", fontSize = 13.sp, color = PaceGray)
                            }
                        }
                    }
                }

                // Invitații primite
                if (pendingWars.isNotEmpty() && (userRole == "Capitan" || userRole == "Vicecapitan")) {
                    item {
                        Text(
                            "📨 Invitații primite",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaceWhite
                        )
                    }
                    items(pendingWars) { war ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "⚔️ ${war.challengerClanName} vă provoacă!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaceWhite
                                )
                                Text(
                                    "Durată: ${war.durationDays} ${if (war.durationDays == 1) "zi" else "zile"}",
                                    fontSize = 13.sp,
                                    color = PaceGray
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { warViewModel.respondToWar(war.id, true) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaceGreen)
                                    ) { Text("✅ Acceptă") }
                                    OutlinedButton(
                                        onClick = {
                                            warToDecline = war
                                            showDeclineDialog = true
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("❌ Refuză", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                // Provoacă alt clan
                if (activeWar == null && (userRole == "Capitan" || userRole == "Vicecapitan")) {
                    item {
                        Text(
                            "Provoacă un clan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaceWhite
                        )
                    }

                    if (allClans.filter { it.id != userClan.id }.isEmpty()) {
                        item {
                            Text(
                                "Nu există alte clanuri momentan.",
                                fontSize = 13.sp,
                                color = PaceGray
                            )
                        }
                    } else {
                        items(allClans.filter { it.id != userClan.id }) { clan ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            clan.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaceWhite
                                        )
                                        Text(
                                            "⚡ ${clan.totalXp} XP • 👥 ${clan.memberCount} membri",
                                            fontSize = 12.sp,
                                            color = PaceGray
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            selectedTargetClan = clan
                                            showChallengeDialog = true
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaceOrange),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text("⚔️ Provoacă", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}