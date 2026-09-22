package com.vivicarmonadev.appmovil_carpinteria.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.LoginScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.LoginViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.RegisterCredentialsScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.RegisterScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.RegisterViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.BottomNavBar
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.BottomNavItem
import com.vivicarmonadev.appmovil_carpinteria.ui.home.HomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.more.MoreScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.more.MoreViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.profile.ProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.projects.ProjectsScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.services.ServicesScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen2
import com.vivicarmonadev.appmovil_carpinteria.ui.navigation.MainViewModel

// RUTAS
object Routes {
    const val WELCOME_1 = "welcome_1"
    const val WELCOME_2 = "welcome_2"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REGISTER_CREDENTIALS = "register_credentials"
    const val HOME = "home"
    const val PROJECTS = "projects"
    const val SERVICES = "services"
    const val MORE = "more"
    const val PROFILE = "profile"
}

// APP NAVIGATION
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // ViewModel compartido entre RegisterScreen y RegisterCredentialsScreen
    val registerViewModel = remember { RegisterViewModel() }

    // ViewModel compartido por TODO el flujo logueado (Home, Más, Perfil)
    val mainViewModel = remember { MainViewModel() }

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME_1
    ) {
        // --- WELCOME ---
        composable(Routes.WELCOME_1) {
            WelcomeScreen(
                onNext = { navController.navigate(Routes.WELCOME_2) }
            )
        }

        composable(Routes.WELCOME_2) {
            WelcomeScreen2(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                }
            )
        }

        // --- LOGIN ---
        composable(Routes.LOGIN) {
            val loginViewModel = remember { LoginViewModel() }

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onGoogleSignIn = { /* TODO: firebase */ }
            )
        }

        // --- REGISTRO PASO 1 ---
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = registerViewModel,
                onContinue = { navController.navigate(Routes.REGISTER_CREDENTIALS) },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // --- REGISTRO PASO 2 ---
        composable(Routes.REGISTER_CREDENTIALS) {
            RegisterCredentialsScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                }
            )
        }

        // --- HOME ---
        composable(Routes.HOME) {
            MainScaffold(
                currentRoute = Routes.HOME,
                navController = navController
            ) {
                HomeScreen(viewModel = mainViewModel)
            }
        }

        // --- PROJECTS ---
        composable(Routes.PROJECTS) {
            MainScaffold(
                currentRoute = Routes.PROJECTS,
                navController = navController
            ) {
                ProjectsScreen()
            }
        }

        // --- SERVICES ---
        composable(Routes.SERVICES) {
            MainScaffold(
                currentRoute = Routes.SERVICES,
                navController = navController
            ) {
                ServicesScreen()
            }
        }

        // --- MORE ---
        composable(Routes.MORE) {
            val moreViewModel = remember { MoreViewModel() }

            MainScaffold(
                currentRoute = Routes.MORE,
                navController = navController
            ) {
                MoreScreen(
                    mainViewModel = mainViewModel,
                    onEditProfile = { navController.navigate(Routes.PROFILE) },
                    onLogout = {
                        moreViewModel.logout {
                            navController.navigate(Routes.WELCOME_1) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        // --- PROFILE ---
        composable(Routes.PROFILE) {
            MainScaffold(
                currentRoute = Routes.PROFILE,
                navController = navController
            ) {
                ProfileScreen(
                    mainViewModel = mainViewModel,
                    onEditProfile = { /* TODO: pantalla editar perfil */ }
                )
            }
        }
    }
}

// SCAFFOLD CON BOTTOM NAV
@Composable
private fun MainScaffold(
    currentRoute: String,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Routes.HOME,
            label = "Inicio",
            icon = Icons.Filled.Home
        ),
        BottomNavItem(
            route = Routes.PROJECTS,
            label = "Proyectos",
            icon = Icons.Filled.Folder
        ),
        BottomNavItem(
            route = Routes.SERVICES,
            label = "Servicios",
            icon = Icons.Filled.Build
        ),
        BottomNavItem(
            route = Routes.MORE,
            label = "Más",
            icon = Icons.Filled.MoreHoriz
        ),
        BottomNavItem(
            route = Routes.PROFILE,
            label = "Perfil",
            icon = Icons.Filled.Person
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            content()
        }

        BottomNavBar(
            items = bottomNavItems,
            currentRoute = currentRoute,
            onItemClick = { route ->
                if (route != currentRoute) {
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}