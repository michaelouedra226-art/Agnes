package com.example.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.icons.AtelierIcons
import com.example.ui.screens.BatchScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.*

sealed class Screen(val route: String, val title: String) {
    object Chat : Screen("chat", "Chat")
    object Batch : Screen("batch", "Batch")
    object Library : Screen("library", "Bibliothèque")
    object Settings : Screen("settings", "Paramètres")
}

@Composable
fun AtelierNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Chat.route
    val context = LocalContext.current
    val activity = context as? Activity

    // Gestion de la touche Retour selon les directives 11.11 :
    // - Si sur écran racine (Chat) : premier appui Toast "Appuyez à nouveau pour quitter", deuxième appui quitte l'application
    // - Si sur écran secondaire : retour à l'écran racine/précédent
    // - Ne coupe jamais les batchs en cours (Foreground Service actif)
    var backPressedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            kotlinx.coroutines.delay(2000)
            backPressedOnce = false
        }
    }

    BackHandler {
        if (currentRoute != Screen.Chat.route) {
            // Écran secondaire -> retour au Chat principal
            navController.navigate(Screen.Chat.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            // Sur l'écran racine Chat
            if (backPressedOnce) {
                activity?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(context, "Appuyez à nouveau pour quitter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val items = listOf(
        Screen.Chat to AtelierIcons.Chat,
        Screen.Batch to AtelierIcons.Batch,
        Screen.Library to AtelierIcons.Library,
        Screen.Settings to AtelierIcons.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = AtelierSurfaceDark,
                contentColor = AtelierTextPrimary,
                tonalElevation = 8.dp
            ) {
                items.forEach { (screen, icon) ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = screen.title,
                                tint = if (selected) AtelierAccent else AtelierTextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) AtelierAccent else AtelierTextMuted
                            )
                        },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = AtelierAccent.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}"),
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        containerColor = AtelierBgDark
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Chat.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(220)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(220)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(220)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(220)) },
            popExitTransition = { fadeOut(animationSpec = tween(180)) }
        ) {
            composable(Screen.Chat.route) {
                ChatScreen(
                    onNavigateToBatch = { prompt ->
                        navController.navigate(Screen.Batch.route)
                    }
                )
            }
            composable(Screen.Batch.route) {
                BatchScreen()
            }
            composable(Screen.Library.route) {
                LibraryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
