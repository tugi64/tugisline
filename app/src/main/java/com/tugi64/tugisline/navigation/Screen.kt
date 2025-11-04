package com.tugi64.tugisline.navigation

sealed class Screen(val route: String) {
    data object MainMenu : Screen("main_menu")
    data object FileExplorer : Screen("file_explorer")
    data object Viewer : Screen("viewer/{fileId}") {
        fun createRoute(fileId: String) = "viewer/$fileId"
    }
    data object Settings : Screen("settings")
    data object LayerManager : Screen("layer_manager")
    data object CloudConnect : Screen("cloud_connect")
    data object About : Screen("about")
    data object Help : Screen("help")
}

