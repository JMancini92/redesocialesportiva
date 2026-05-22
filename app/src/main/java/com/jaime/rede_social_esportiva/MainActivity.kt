package com.jaime.rede_social_esportiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jaime.rede_social_esportiva.presentation.screens.FeedScreen
import com.jaime.rede_social_esportiva.presentation.screens.LoginScreen
import com.jaime.rede_social_esportiva.presentation.screens.ProfileScreen
import com.jaime.rede_social_esportiva.presentation.screens.RegisterScreen
import com.jaime.rede_social_esportiva.ui.theme.RedesocialesportivaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedesocialesportivaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigateUp()
                }
            )
        }
        composable("feed") {
            FeedScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("feed") { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToHome = {
                    navController.navigate("feed") {
                        popUpTo("feed") { inclusive = true }
                    }
                },
                onNavigateToSearch = {
                    // TODO: Implementar tela de busca
                },
                onNavigateToProfile = {
                    // Já está na tela de perfil
                }
            )
        }
    }
}
