package com.example.paceup.ui.run

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.paceup.model.Run
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.RunViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RunScreen(
    navController: NavController,
    runViewModel: RunViewModel = viewModel()
) {
    val context = LocalContext.current
    val distanceKm by runViewModel.distanceKm.collectAsState()
    val isRunning by runViewModel.isRunning.collectAsState()
    val isSaved by runViewModel.isSaved.collectAsState()
    val elapsedSeconds by runViewModel.elapsedSeconds.collectAsState()
    val runHistory by runViewModel.runHistory.collectAsState()
    val isLoadingHistory by runViewModel.isLoadingHistory.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var lastLocation by remember { mutableStateOf<Location?>(null) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        runViewModel.loadHistory()
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            runViewModel.updateElapsedSeconds(elapsedSeconds + 1)
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val newLocation = result.lastLocation ?: return
                lastLocation?.let { last ->
                    val distanceMeters = last.distanceTo(newLocation)
                    val newKm = distanceKm + distanceMeters / 1000.0
                    runViewModel.updateDistance(newKm)
                }
                lastLocation = newLocation
            }
        }
    }

    LaunchedEffect(isRunning) {
        if (isRunning && hasPermission) {
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 3000L
            ).build()
            try {
                fusedLocationClient.requestLocationUpdates(request, locationCallback, null)
            } catch (e: SecurityException) { }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val xpEarned = runViewModel.calculateXp(distanceKm)
    val paceString = runViewModel.getPaceString(distanceKm, elapsedSeconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaceDark)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PaceCardDark,
            contentColor = PaceGreen
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("🏃 Run", color = if (selectedTab == 0) PaceGreen else PaceGray) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("📊 Istoric", color = if (selectedTab == 1) PaceGreen else PaceGray) }
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "🏃 Run Mode",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaceWhite
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = PaceCardDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "%02d:%02d".format(minutes, seconds),
                                    fontSize = 52.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaceGreen
                                )
                                Text(text = "Timp", fontSize = 14.sp, color = PaceGray)
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatRunCard(
                                modifier = Modifier.weight(1f),
                                value = "%.3f".format(distanceKm),
                                unit = "km"
                            )
                            StatRunCard(
                                modifier = Modifier.weight(1f),
                                value = "+$xpEarned",
                                unit = "XP",
                                valueColor = PaceYellow
                            )
                            StatRunCard(
                                modifier = Modifier.weight(1f),
                                value = paceString.split(" ")[0],
                                unit = "min/km",
                                valueColor = PaceOrange
                            )
                        }
                    }

                    item {
                        if (!hasPermission) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PaceOrange),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("📍 Acordă permisiune GPS", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item {
                        if (isSaved) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = PaceGreen.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "✅ Run salvat!",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaceGreen
                                    )
                                    Text(
                                        "+$xpEarned XP câștigat!",
                                        fontSize = 16.sp,
                                        color = PaceYellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "%.3f km în %02d:%02d".format(distanceKm, minutes, seconds),
                                        fontSize = 14.sp,
                                        color = PaceGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    runViewModel.reset()
                                    runViewModel.loadHistory()
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PaceCardDark)
                            ) {
                                Text("🔄 Run nou", color = PaceWhite, fontWeight = FontWeight.Bold)
                            }
                        } else if (hasPermission) {
                            Button(
                                onClick = {
                                    if (isRunning) {
                                        runViewModel.setRunning(false)
                                        if (distanceKm > 0.01) runViewModel.saveRun()
                                    } else {
                                        lastLocation = null
                                        runViewModel.setRunning(true)
                                    }
                                },
                                modifier = Modifier.size(130.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRunning) PaceOrange else PaceGreen
                                )
                            ) {
                                Text(
                                    text = if (isRunning) "STOP" else "START",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                if (isLoadingHistory) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PaceGreen)
                    }
                } else if (runHistory.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🏃", fontSize = 48.sp)
                            Text(
                                "Nicio alergare încă!",
                                fontSize = 18.sp,
                                color = PaceGray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Apasă START și începe prima ta alergare.",
                                fontSize = 14.sp,
                                color = PaceGray
                            )
                        }
                    }
                } else {
                    val totalKm = runHistory.sumOf { it.distanceKm }
                    val totalXp = runHistory.sumOf { it.xpEarned }
                    val totalRuns = runHistory.size
                    val avgPace = if (runHistory.isNotEmpty()) {
                        runHistory.filter { it.avgPaceSecondsPerKm > 0 }
                            .map { it.avgPaceSecondsPerKm }
                            .average().toInt()
                    } else 0
                    val avgPaceMin = avgPace / 60
                    val avgPaceSec = avgPace % 60
                    val bestRun = runHistory.maxByOrNull { it.distanceKm }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = PaceCardDark)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "📊 Statistici Totale",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaceWhite
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        StatTotalItem("$totalRuns", "Alergări")
                                        StatTotalItem("%.3f".format(totalKm), "km Total")
                                        StatTotalItem("$totalXp", "XP Total")
                                    }
                                    HorizontalDivider(color = PaceGray.copy(alpha = 0.2f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        StatTotalItem(
                                            if (avgPace > 0) "%d:%02d".format(avgPaceMin, avgPaceSec) else "--:--",
                                            "Pace Mediu"
                                        )
                                        StatTotalItem(
                                            "%.3f".format(bestRun?.distanceKm ?: 0.0),
                                            "Cel Mai Lung"
                                        )
                                        StatTotalItem(
                                            "%.3f".format(totalKm / totalRuns),
                                            "Medie/Run"
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "Alergări Recente",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaceWhite
                            )
                        }

                        items(runHistory) { run ->
                            RunHistoryCard(run = run)
                        }

                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRunCard(
    modifier: Modifier = Modifier,
    value: String,
    unit: String,
    valueColor: androidx.compose.ui.graphics.Color = PaceWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaceCardDark)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
            Text(text = unit, fontSize = 11.sp, color = PaceGray)
        }
    }
}

@Composable
fun StatTotalItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PaceWhite
        )
        Text(text = label, fontSize = 11.sp, color = PaceGray)
    }
}

@Composable
fun RunHistoryCard(run: Run) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val date = dateFormat.format(Date(run.timestamp))
    val durationMin = run.durationSeconds / 60
    val durationSec = run.durationSeconds % 60
    val paceMin = run.avgPaceSecondsPerKm / 60
    val paceSec = run.avgPaceSecondsPerKm % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaceCardDark)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏃 %.3f km".format(run.distanceKm),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceWhite
                )
                Text(
                    text = "+${run.xpEarned} XP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaceYellow
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RunStatChip(icon = "⏱️", value = "%02d:%02d".format(durationMin, durationSec))
                RunStatChip(
                    icon = "⚡",
                    value = if (run.avgPaceSecondsPerKm > 0)
                        "%d:%02d min/km".format(paceMin, paceSec)
                    else "--:--"
                )
            }

            Text(
                text = date,
                fontSize = 12.sp,
                color = PaceGray
            )
        }
    }
}

@Composable
fun RunStatChip(icon: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 13.sp)
        Text(value, fontSize = 13.sp, color = PaceGray)
    }
}