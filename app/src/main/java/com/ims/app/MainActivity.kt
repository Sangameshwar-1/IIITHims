package com.ims.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ims.app.ui.navigation.ImsNavGraph
import com.ims.app.ui.navigation.Screen
import com.ims.app.ui.screens.splash.SplashScreen
import com.ims.app.ui.theme.*

/**
 * Main entry point for the IMS application.
 * Sets up the Compose UI with bottom navigation and the nav graph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            ImsTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onComplete = { showSplash = false })
                } else {
                    ImsApp()
                }
            }
        }
    }
}

/**
 * Root composable with bottom navigation bar and nav host.
 */
@Composable
fun ImsApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if bottom bar should be visible (hide on sub-screens)
    val showBottomBar = currentDestination?.route in Screen.bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                ImsBottomBar(
                    items = Screen.bottomNavItems,
                    currentRoute = currentDestination?.route,
                    onItemClick = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ImsNavGraph(navController = navController)
        }
    }
}

/**
 * Custom bottom navigation bar with premium styling.
 * Features animated selection indicators and icon tinting.
 */
@Composable
fun ImsBottomBar(
    items: List<Screen>,
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            val iconColor by animateColorAsState(
                targetValue = if (selected) Primary else OnSurfaceVariant.copy(alpha = 0.5f),
                label = "navIconColor"
            )

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(screen) },
                icon = {
                    screen.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.title,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = iconColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
