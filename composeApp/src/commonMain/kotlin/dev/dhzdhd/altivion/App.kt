package dev.dhzdhd.altivion

import altivion.composeapp.generated.resources.Res
import altivion.composeapp.generated.resources.map
import altivion.composeapp.generated.resources.search
import altivion.composeapp.generated.resources.settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.dhzdhd.altivion.home.viewmodels.HomeViewModel
import dev.dhzdhd.altivion.home.views.HomeView
import dev.dhzdhd.altivion.search.viewmodels.SearchViewModel
import dev.dhzdhd.altivion.search.views.SearchView
import dev.dhzdhd.altivion.settings.viewmodels.SettingsViewModel
import dev.dhzdhd.altivion.settings.viewmodels.ThemeMode
import dev.dhzdhd.altivion.settings.views.SettingsView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

sealed interface TabPage {
  @Serializable data object Home : TabPage

  @Serializable data object Search : TabPage

  @Serializable data object Settings : TabPage

  companion object {
    fun valueOf(it: String): TabPage {
      return when (it) {
        "Home" -> Home
        "Search" -> Search
        "Settings" -> Settings
        else -> Home
      }
    }
  }
}

private val TabPageSaver =
    Saver<TabPage, String>(save = { it.toString() }, restore = { TabPage.valueOf(it) })

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
  val startPage: TabPage = TabPage.Home
  var selectedPage by rememberSaveable(stateSaver = TabPageSaver) { mutableStateOf(startPage) }
  val navController = rememberNavController()
  val snackBarHostState = remember { SnackbarHostState() }

  val homeViewModel = koinViewModel<HomeViewModel>()
  val searchViewModel = koinViewModel<SearchViewModel>()
  val settingsViewModel = koinViewModel<SettingsViewModel>()

  val settings by settingsViewModel.settings.collectAsState()

  MaterialTheme(
      colorScheme =
          when (settings.themeMode) {
            ThemeMode.Light -> lightColorScheme()
            ThemeMode.Dark -> darkColorScheme()
          }) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
              if (selectedPage !is TabPage.Home)
                  MediumTopAppBar(title = { Text(selectedPage.toString()) })
            },
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
                          painter = painterResource(Res.drawable.map),
                          contentDescription = "Map",
                      )
                    },
                    label = { Text("Map") })
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
            }) { contentPadding ->
              NavHost(navController = navController, startDestination = TabPage.Home) {
                composable<TabPage.Home> {
                  LaunchedEffect(Unit) {
                    homeViewModel.snackBarEvents.collectLatest {
                      snackBarHostState.showSnackbar(it)
                    }
                  }

                  HomeView(
                      viewModel = homeViewModel,
                      settings = settings,
                      contentPadding = contentPadding)
                }
                composable<TabPage.Search> {
                  LaunchedEffect(Unit) {
                    searchViewModel.snackBarEvents.collectLatest {
                      snackBarHostState.showSnackbar(it)
                    }
                  }

                  SearchView(viewModel = searchViewModel, contentPadding = contentPadding)
                }
                composable<TabPage.Settings> {
                  LaunchedEffect(Unit) {
                    settingsViewModel.snackBarEvents.collectLatest {
                      snackBarHostState.showSnackbar(it)
                    }
                  }

                  SettingsView(viewModel = settingsViewModel, contentPadding = contentPadding)
                }
              }
            }
      }
}
