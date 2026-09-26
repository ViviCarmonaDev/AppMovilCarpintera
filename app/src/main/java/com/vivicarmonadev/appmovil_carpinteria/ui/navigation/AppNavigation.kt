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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
import com.vivicarmonadev.appmovil_carpinteria.ui.profile.EditProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.profile.EditProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.profile.ProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.projects.ProjectsScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.services.ServicesScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen2
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.CompleteProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.CompleteProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.profile.CarpenterProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.profile.CarpenterProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.ui.projects.PortfolioViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.projects.edit.EditPortfolioItemScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.projects.edit.EditPortfolioItemViewModel

object Routes {
    const val WELCOME_1 = "welcome_1"
    const val WELCOME_2 = "welcome_2"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REGISTER_CREDENTIALS = "register_credentials"
    const val COMPLETE_PROFILE = "complete_profile"
    const val CARPENTER_PROFILE = "carpenter_profile"
    const val HOME = "home"
    const val PROJECTS = "projects"
    const val SERVICES = "services"
    const val MORE = "more"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val EDIT_PORTFOLIO_ITEM = "edit_portfolio_item"
}

private const val WEB_CLIENT_ID = "73930140303-882u6cn6rd0j4dl1uqi3fg6i9ta4i1n0.apps.googleusercontent.com"

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val registerViewModel = remember { RegisterViewModel() }
    val mainViewModel = remember { MainViewModel() }

    // Observamos el usuario actual para saber si tiene el perfil completo
    val currentUser by mainViewModel.currentUser.collectAsStateWithLifecycle()

    // Auto-navegación: si el usuario está logueado pero no completó el perfil,
    // lo mandamos a Completar Perfil (solo si NO estamos en pantallas de auth).
    LaunchedEffect(currentUser) {
        val user = currentUser
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // Rutas de autenticación donde NO hay que redirigir
        val authRoutes = listOf(
            Routes.WELCOME_1,
            Routes.WELCOME_2,
            Routes.LOGIN,
            Routes.REGISTER,
            Routes.REGISTER_CREDENTIALS
        )

        if (user != null &&
            !user.profileCompleted &&
            currentRoute !in authRoutes &&
            currentRoute != Routes.COMPLETE_PROFILE
        ) {
            navController.navigate(Routes.COMPLETE_PROFILE) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
                serverClientId = WEB_CLIENT_ID,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME_1) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
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
                    // Chequear el rol para decidir a dónde ir
                    val user = currentUser
                    val destination = if (user?.role == UserRole.CARPENTER) {
                        Routes.CARPENTER_PROFILE
                    } else {
                        Routes.HOME
                    }
                    navController.navigate(destination) {
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

        // --- COMPLETAR PERFIL ---
        composable(Routes.COMPLETE_PROFILE) {
            val completeProfileViewModel = remember { CompleteProfileViewModel() }

            // Inicializamos con los datos del usuario actual (de Google)
            LaunchedEffect(currentUser) {
                currentUser?.let { completeProfileViewModel.initialize(it) }
            }
                  CompleteProfileScreen(
                viewModel = completeProfileViewModel,
                onCompleteSuccess = {
                    // Chequear el rol para decidir a dónde ir
                    val user = currentUser
                    val destination = if (user?.role == UserRole.CARPENTER) {
                        Routes.CARPENTER_PROFILE
                    } else {
                        Routes.HOME
                    }

                    navController.navigate(destination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- PERFIL DEL TALLER (solo para carpinteros) ---
        composable(Routes.CARPENTER_PROFILE) {
            val carpenterViewModel = remember { CarpenterProfileViewModel() }

            LaunchedEffect(currentUser) {
                currentUser?.let { carpenterViewModel.initialize(it.uid) }
            }

            CarpenterProfileScreen(
                viewModel = carpenterViewModel,
                onSaveSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
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
            val portfolioViewModel = remember { PortfolioViewModel() }

            MainScaffold(
                currentRoute = Routes.PROJECTS,
                navController = navController
            ) {
                ProjectsScreen(
                    viewModel = portfolioViewModel,
                    onCreateClick = {
                        navController.navigate(Routes.EDIT_PORTFOLIO_ITEM)
                    },
                    onEditClick = { item ->
                        navController.navigate("${Routes.EDIT_PORTFOLIO_ITEM}?itemId=${item.id}")
                    }
                )
            }
        }

        // --- CREAR/EDITAR TRABAJO ---
        composable(
            route = "${Routes.EDIT_PORTFOLIO_ITEM}?itemId={itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")

            val editViewModel = remember {
                EditPortfolioItemViewModel().apply {
                    if (itemId.isNullOrBlank()) {
                        initializeCreate()
                    } else {
                        initializeEdit(itemId)
                    }
                }
            }

            EditPortfolioItemScreen(
                viewModel = editViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
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
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
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
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) }
                )
            }
        }

        // --- EDIT PROFILE ---
        composable(Routes.EDIT_PROFILE) {
            val editProfileViewModel = remember { EditProfileViewModel() }
            val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

            // Cuando carga el usuario, inicializamos el ViewModel con sus datos
            LaunchedEffect(user) {
                user?.let { editProfileViewModel.initialize(it) }
            }

            EditProfileScreen(
                viewModel = editProfileViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    // Volvemos a la pantalla anterior (Perfil o Más)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun MainScaffold(
    currentRoute: String,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, "Inicio", Icons.Filled.Home),
        BottomNavItem(Routes.PROJECTS, "Proyectos", Icons.Filled.Folder),
        BottomNavItem(Routes.SERVICES, "Servicios", Icons.Filled.Build),
        BottomNavItem(Routes.MORE, "Más", Icons.Filled.MoreHoriz),
        BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
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