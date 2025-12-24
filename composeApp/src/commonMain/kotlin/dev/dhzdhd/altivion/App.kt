package dev.dhzdhd.altivion

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.home
import altivion.composeapp.generated.resources.search
import altivion.composeapp.generated.resources.settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.dhzdhd.altivion.home.views.HomeView
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

sealed interface TabPage {
    @Serializable
    object Home : TabPage

    @Serializable
    object Search : TabPage

    @Serializable
    object Settings : TabPage
}

@Composable
@Preview
fun App() {
    val startPage: TabPage = TabPage.Home
    var selectedPage by rememberSaveable {
        mutableStateOf(startPage)
    }
    val navController = rememberNavController()

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedPage is TabPage.Home,
                        onClick = {
                            selectedPage = TabPage.Home
                            navController.navigate(TabPage.Home)

                        },
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.home),
                                contentDescription = "Home",
                            )
                        },
                        label = { Text("Home") })
                    NavigationBarItem(
                        selected = selectedPage is TabPage.Search,
                        onClick = {
                            selectedPage = TabPage.Search
                            navController.navigate(TabPage.Search)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.search),
                                contentDescription = "Search",
                            )
                        },
                        label = { Text("Search") })
                    NavigationBarItem(
                        selected = selectedPage is TabPage.Settings,
                        onClick = {
                            selectedPage = TabPage.Settings
                            navController.navigate(TabPage.Settings)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.settings),
                                contentDescription = "Settings",
                            )
                        },
                        label = { Text("Settings") })
                }
            }
        ) { contentPadding ->
            NavHost(navController = navController, startDestination = TabPage.Home) {
                composable<TabPage.Home> { HomeView(contentPadding = contentPadding) }
                composable<TabPage.Search> { Text("Search") }
                composable<TabPage.Settings> { Text("Settings") }
            }
        }
    }
}