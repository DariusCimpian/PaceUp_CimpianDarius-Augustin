package com.example.paceup.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paceup.ui.auth.LoginScreen
import com.example.paceup.ui.auth.RegisterScreen
import com.example.paceup.ui.clan.ClanScreen
import com.example.paceup.ui.home.HomeScreen
import com.example.paceup.ui.leaderboard.LeaderboardScreen
import com.example.paceup.ui.profile.ProfileScreen
import com.example.paceup.ui.run.RunScreen
import com.example.paceup.ui.splash.SplashScreen
import com.example.paceup.ui.theme.PaceGreen
import com.example.paceup.ui.theme.PaceGray
import com.example.paceup.ui.theme.PaceCardDark
import com.example.paceup.viewmodel.ClanViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Run : Screen("run")
    object Clan : Screen("clan")
    object Leaderboard : Screen("leaderboard")
    object Profile : Screen("profile")
    object Splash : Screen("splash")
}

data class BottomNavItem(
    val label: String,
    val icon: String,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", "🏠", Screen.Home.route),
    BottomNavItem("Run", "🏃", Screen.Run.route),
    BottomNavItem("Clan", "⚔️", Screen.Clan.route),
    BottomNavItem("Top", "🏆", Screen.Leaderboard.route),
    BottomNavItem("Profil", "👤", Screen.Profile.route),
)

val authRoutes = listOf(Screen.Login.route, Screen.Register.route)

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val showBottomBar = currentRoute !in authRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = PaceCardDark
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Text(text = item.icon, fontSize = 20.sp)
                            },
                            label = {
                                Text(text = item.label, color = if (currentRoute == item.route) PaceGreen else PaceGray)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF2E7D32).copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Run.route) {
                RunScreen(navController)
            }
            composable(Screen.Clan.route) {
                val clanViewModel: ClanViewModel = viewModel(it)
                ClanScreen(navController, clanViewModel)
            }
            composable(Screen.Leaderboard.route) {
                 LeaderboardScreen(navController)
            }
            composable(Screen.Profile.route) {
                  ProfileScreen(navController)
            }
        }
    }
}