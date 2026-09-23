package com.sk.phonecleaner_aicleaner.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Permission : Screen("permission")
    object Main : Screen("main")
    object Scanning : Screen("scanning")
    object Cleaning : Screen("cleaning")
    object Boost : Screen("boost")
    object Duplicate : Screen("duplicate")
    object WaCleaner : Screen("wa_cleaner")
    object Battery : Screen("battery")
    object Compress : Screen("compress")
    object Apps : Screen("apps")
    object Settings : Screen("settings")
}