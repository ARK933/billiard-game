package com.example.billiardgame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.billiardgame.ui.cueshifts.CueStickScreen
import com.example.billiardgame.ui.game.GameScreen
import com.example.billiardgame.ui.menu.MainMenuScreen
import com.example.billiardgame.ui.stats.ScoreboardScreen

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Game : Screen("game")
    object CueSticks : Screen("cue_sticks")
    object Scoreboard : Screen("scoreboard")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route,
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Game.route) {
            GameScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CueSticks.route) {
            CueStickScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Scoreboard.route) {
            ScoreboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
