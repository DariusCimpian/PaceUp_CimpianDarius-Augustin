package com.example.paceup.ui.clan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.paceup.model.Clan
import com.example.paceup.model.ClanMember
import com.example.paceup.ui.chat.ChatScreen
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.ClanViewModel

@Composable
fun ClanScreen(
    navController: NavController,
    clanViewModel: ClanViewModel = viewModel()
) {
    val clans by clanViewModel.clans.collectAsState()
    val userClan by clanViewModel.userClan.collectAsState()
    val members by clanViewModel.members.collectAsState()
    val selectedClan by clanViewModel.selectedClan.collectAsState()
    val selectedClanMembers by clanViewModel.selectedClanMembers.collectAsState()
    val isLoading by clanViewModel.isLoading.collectAsState()
    val message by clanViewModel.message.collectAsState()
    val userRole by clanViewModel.userRole.collectAsState()
    val currentUid by clanViewModel.currentUid.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var clanName by remember { mutableStateOf("") }
    var clanDesc by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { clanViewModel.loadData() }

    // Dialog creare clan
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = PaceCardDark,
            title = { Text("Creează Clan", color = PaceWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = clanName,
                        onValueChange = { clanName = it },
                        label = { Text("Nume clan", color = PaceGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaceGreen,
                            unfocusedBorderColor = PaceGray,
                            focusedTextColor = PaceWhite,
                            unfocusedTextColor = PaceWhite
                        )
                    )
                    OutlinedTextField(
                        value = clanDesc,
                        onValueChange = { clanDesc = it },
                        label = { Text("Descriere", color = PaceGray) },
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
                        clanViewModel.createClan(clanName, clanDesc)
                        showCreateDialog = false
                        clanName = ""
                        clanDesc = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PaceGreen)
                ) { Text("Creează") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Anulează", color = PaceGray)
                }
            }
        )
    }

    // Dialog parasire clan
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            containerColor = PaceCardDark,
            title = { Text("Părăsești clanul?", color = PaceWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Ești sigur că vrei să părăsești ${userClan?.name}?",
                    color = PaceGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        clanViewModel.leaveClan()
                        showLeaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Părăsesc") }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Anulează", color = PaceGray)
                }
            }
        )
    }

    // Dialog stergere clan
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = PaceCardDark,
            title = {
                Text("Ștergi clanul?", color = PaceWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Toți membrii vor fi eliminați din clan. Această acțiune nu poate fi anulată!",
                    color = PaceGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        clanViewModel.deleteClan()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Șterge") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anulează", color = PaceGray)
                }
            }
        )
    }

    // Ecran detalii clan selectat
    if (selectedClan != null) {
        ClanDetailScreen(
            clan = selectedClan!!,
            members = selectedClanMembers,
            isInClan = userClan != null,
            onBack = { clanViewModel.clearSelectedClan() },
            onJoin = { clanViewModel.joinClan(selectedClan!!.id) }
        )
        return
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
            if (userClan == null) {
                // Userul nu e in niciun clan
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "⚔️ Clanuri",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaceWhite
                        )
                    }
                    item {
                        Button(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PacePurple)
                        ) {
                            Text(
                                "⚔️ Creează un clan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    item {
                        Text(
                            "Toate clanurile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaceWhite
                        )
                    }
                    items(clans) { clan ->
                        ClanListCard(
                            clan = clan,
                            onClick = { clanViewModel.selectClan(clan) },
                            onJoin = null,
                            showJoin = true
                        )
                    }
                }
            } else {
                // Userul e in clan
                Column(modifier = Modifier.fillMaxSize()) {

                    // Header clan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PaceCardDark)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = userClan!!.name,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaceWhite
                                    )
                                    Text(
                                        text = userClan!!.description,
                                        fontSize = 13.sp,
                                        color = PaceGray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (userRole) {
                                                "Capitan" -> PacePurple.copy(alpha = 0.2f)
                                                "Vicecapitan" -> PaceOrange.copy(alpha = 0.2f)
                                                "Veteran" -> PaceYellow.copy(alpha = 0.2f)
                                                else -> PaceGray.copy(alpha = 0.2f)
                                            },
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when (userRole) {
                                            "Capitan" -> "👑 $userRole"
                                            "Vicecapitan" -> "⚡ $userRole"
                                            "Veteran" -> "🛡️ $userRole"
                                            else -> "👤 $userRole"
                                        },
                                        fontSize = 12.sp,
                                        color = PaceWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${userClan!!.totalXp}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaceYellow
                                    )
                                    Text("XP Total", fontSize = 11.sp, color = PaceGray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${userClan!!.memberCount}/${userClan!!.maxMembers}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaceGreen
                                    )
                                    Text("Membri", fontSize = 11.sp, color = PaceGray)
                                }
                            }
                        }
                    }

                    // Tab Row
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = PaceCardDark,
                        contentColor = PaceGreen,
                        edgePadding = 0.dp
                    ) {
                        listOf("🏰 Clan", "👥 Membri", "💬 Chat", "⚔️ War", "🌍 Clanuri")
                            .forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            color = if (selectedTab == index) PaceGreen else PaceGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                )
                            }
                    }

                    // Continut tab
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedTab) {
                            0 -> {
                                // Tab Clan
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Mesaj status
                                    item {
                                        message?.let {
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = PaceGreen.copy(alpha = 0.15f)
                                                )
                                            ) {
                                                Text(
                                                    it,
                                                    modifier = Modifier.padding(12.dp),
                                                    color = PaceGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Paraseste clan (doar non-capitan)
                                    item {
                                        if (userRole != "Capitan") {
                                            Button(
                                                onClick = { showLeaveDialog = true },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(52.dp),
                                                shape = RoundedCornerShape(14.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.error
                                                )
                                            ) {
                                                Text(
                                                    "🚪 Părăsește Clanul",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Sectiune capitan
                                    item {
                                        if (userRole == "Capitan") {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = PaceCardDark
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(
                                                            "👑 Opțiuni Capitan",
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = PacePurple
                                                        )
                                                        Text(
                                                            "Gestionează membrii din tab-ul 👥 Membri.",
                                                            fontSize = 13.sp,
                                                            color = PaceGray
                                                        )
                                                        Text(
                                                            "Transferă rolul de capitan înainte să ștergi clanul.",
                                                            fontSize = 13.sp,
                                                            color = PaceGray
                                                        )
                                                    }
                                                }

                                                Button(
                                                    onClick = { showDeleteDialog = true },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(52.dp),
                                                    shape = RoundedCornerShape(14.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.error
                                                    )
                                                ) {
                                                    Text(
                                                        "🗑️ Șterge Clanul",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            1 -> {
                                // Tab Membri
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(members) { member ->
                                        MemberCardWithRole(
                                            member = member,
                                            isCurrentUser = member.uid == currentUid,
                                            userRole = userRole,
                                            onPromote = { newRole ->
                                                clanViewModel.updateMemberRole(member.uid, newRole)
                                            },
                                            onDemote = { newRole ->
                                                clanViewModel.updateMemberRole(member.uid, newRole)
                                            }
                                        )
                                    }
                                }
                            }

                            2 -> {
                                // Tab Chat — direct, fara LazyColumn
                                ChatScreen(
                                    chatPath = "clans/${userClan!!.id}/chat",
                                    title = "💬 Chat Clan"
                                )
                            }

                            3 -> {
                                // Tab War — direct, fara LazyColumn
                                WarScreen(
                                    userClan = userClan!!,
                                    allClans = clans,
                                    userRole = userRole
                                )
                            }

                            4 -> {
                                // Tab Clanuri
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(clans) { clan ->
                                        ClanListCard(
                                            clan = clan,
                                            onClick = { clanViewModel.selectClan(clan) },
                                            onJoin = null,
                                            showJoin = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCardWithRole(
    member: ClanMember,
    isCurrentUser: Boolean,
    userRole: String,
    onPromote: (String) -> Unit,
    onDemote: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) PaceGreen.copy(alpha = 0.1f) else PaceCardDark
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PaceGreen.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏃", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = if (isCurrentUser) "${member.username} (Tu)" else member.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaceWhite
                    )
                    Text(
                        text = when (member.role) {
                            "Capitan" -> "👑 Capitan"
                            "Vicecapitan" -> "⚡ Vicecapitan"
                            "Veteran" -> "🛡️ Veteran"
                            else -> "👤 Membru"
                        },
                        fontSize = 12.sp,
                        color = when (member.role) {
                            "Capitan" -> PacePurple
                            "Vicecapitan" -> PaceOrange
                            "Veteran" -> PaceYellow
                            else -> PaceGray
                        }
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${member.xp} XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaceYellow
                    )
                    Text("Lv. ${member.level}", fontSize = 12.sp, color = PaceGray)
                }

                // Buton manage rol — doar capitanul, doar pentru non-capitan
                if (userRole == "Capitan" && !isCurrentUser && member.role != "Capitan") {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Text("⚙️", fontSize = 18.sp)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(PaceCardDark)
                        ) {
                            when (member.role) {
                                "Membru" -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text("🛡️ Promovează Veteran", color = PaceYellow)
                                        },
                                        onClick = { onPromote("Veteran"); showMenu = false }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text("⚡ Promovează Vicecapitan", color = PaceOrange)
                                        },
                                        onClick = { onPromote("Vicecapitan"); showMenu = false }
                                    )
                                }
                                "Veteran" -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text("⚡ Promovează Vicecapitan", color = PaceOrange)
                                        },
                                        onClick = { onPromote("Vicecapitan"); showMenu = false }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text("👤 Demotează la Membru", color = PaceGray)
                                        },
                                        onClick = { onDemote("Membru"); showMenu = false }
                                    )
                                }
                                "Vicecapitan" -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text("🛡️ Demotează la Veteran", color = PaceYellow)
                                        },
                                        onClick = { onDemote("Veteran"); showMenu = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClanDetailScreen(
    clan: Clan,
    members: List<ClanMember>,
    isInClan: Boolean,
    onBack: () -> Unit,
    onJoin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaceDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PaceCardDark)
                        .padding(20.dp)
                ) {
                    Column {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = PaceWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            clan.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaceWhite
                        )
                        Text(clan.description, fontSize = 14.sp, color = PaceGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${clan.totalXp}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaceYellow
                                )
                                Text("XP", fontSize = 11.sp, color = PaceGray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${clan.memberCount}/${clan.maxMembers}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaceGreen
                                )
                                Text("Membri", fontSize = 11.sp, color = PaceGray)
                            }
                        }
                        if (!isInClan && clan.memberCount < clan.maxMembers) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onJoin(); onBack() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PaceGreen)
                            ) {
                                Text(
                                    "⚔️ Alătură-te Clanului",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        } else if (clan.memberCount >= clan.maxMembers) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "🚫 Clan plin!",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Membri",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceWhite,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(members) { member ->
                MemberCard(
                    member = member,
                    isCurrentUser = false,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun MemberCard(
    member: ClanMember,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) PaceGreen.copy(alpha = 0.1f) else PaceCardDark
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PaceGreen.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏃", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = if (isCurrentUser) "${member.username} (Tu)" else member.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaceWhite
                    )
                    Text(
                        text = when (member.role) {
                            "Capitan" -> "👑 Capitan"
                            "Vicecapitan" -> "⚡ Vicecapitan"
                            "Veteran" -> "🛡️ Veteran"
                            else -> "👤 Membru"
                        },
                        fontSize = 12.sp,
                        color = when (member.role) {
                            "Capitan" -> PacePurple
                            "Vicecapitan" -> PaceOrange
                            "Veteran" -> PaceYellow
                            else -> PaceGray
                        }
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${member.xp} XP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceYellow
                )
                Text("Lv. ${member.level}", fontSize = 12.sp, color = PaceGray)
            }
        }
    }
}

@Composable
fun ClanListCard(
    clan: Clan,
    onClick: () -> Unit,
    onJoin: (() -> Unit)?,
    showJoin: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    clan.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceWhite
                )
                Text(clan.description, fontSize = 12.sp, color = PaceGray, maxLines = 1)
                Text(
                    "👥 ${clan.memberCount}/${clan.maxMembers} • ⚡ ${clan.totalXp} XP",
                    fontSize = 12.sp,
                    color = PaceGray
                )
            }
            if (clan.memberCount < clan.maxMembers) {
                Text("›", fontSize = 24.sp, color = PaceGreen, fontWeight = FontWeight.Bold)
            } else {
                Text("🚫", fontSize = 18.sp)
            }
        }
    }
}