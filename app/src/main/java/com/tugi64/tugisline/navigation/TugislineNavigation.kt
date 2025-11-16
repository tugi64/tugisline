package com.tugi64.tugisline.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tugi64.tugisline.ui.screens.about.AboutScreen
import com.tugi64.tugisline.ui.screens.cloud.CloudConnectScreen
import com.tugi64.tugisline.ui.screens.help.HelpScreen
import com.tugi64.tugisline.ui.screens.layers.LayerManagerScreen
import com.tugi64.tugisline.ui.screens.mainmenu.MainMenuScreen
import com.tugi64.tugisline.ui.screens.settings.SettingsScreen
import com.tugi64.tugisline.ui.screens.viewer.ViewerScreen

@Composable
fun TugislineNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route,
        modifier = modifier
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateToFileExplorer = { navController.navigate(Screen.FileExplorer.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToCloud = { navController.navigate(Screen.CloudConnect.route) },
                onOpenFile = { fileId -> navController.navigate(Screen.Viewer.createRoute(fileId)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Viewer.route,
            arguments = listOf(navArgument("fileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val fileId = backStackEntry.arguments?.getString("fileId") ?: ""
            ViewerScreen(
                fileId = fileId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLayers = { navController.navigate(Screen.LayerManager.route) }
            )
        }

        composable(Screen.LayerManager.route) {
            LayerManagerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CloudConnect.route) {
            CloudConnectScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FileExplorer.route) {
            // Şimdilik FileExplorer yerine MainMenu'ye yönlendir
            MainMenuScreen(
                onNavigateToFileExplorer = { /* Zaten buradayız */ },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToCloud = { navController.navigate(Screen.CloudConnect.route) },
                onOpenFile = { fileId -> navController.navigate(Screen.Viewer.createRoute(fileId)) }
            )
        }
    }
}

