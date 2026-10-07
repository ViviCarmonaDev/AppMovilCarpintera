package com.vivicarmonadev.appmovil_carpinteria.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
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
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.login.LoginScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.login.LoginViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.register.RegisterCredentialsScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.register.RegisterScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.register.RegisterViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.BottomNavBar
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.BottomNavItem
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.HomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.more.MoreScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.more.MoreViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.profile.EditProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.profile.EditProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.profile.ProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.view.PortfolioScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen2
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.completeProfileGoogle.completeProfileGoogleViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.completeProfileGoogle.CompleteProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.completeProfileTaller.CarpenterProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.completeProfileTaller.completeProfileTallerViewModel
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create.CreatePortfolioScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create.CreatePortfolioViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.view.PortfolioViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.edit.EditPortfolioItemScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.edit.EditPortfolioViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.CarpenterPublicProfileScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.CarpenterPublicProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.HomeViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos.PedidosScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos.PedidosViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos.edit.EditPedidoScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.client.pedidos.edit.EditPedidoViewModel
import androidx.compose.material.icons.filled.Inbox
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components.DeletePortfolioScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail.DetailPortfolioViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail.DetailPortfolioScreen

object Routes {
    const val WELCOME_1 = "welcome_1"
    const val WELCOME_2 = "welcome_2"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REGISTER_CREDENTIALS = "register_credentials"
    const val COMPLETE_PROFILE = "complete_profile"
    const val CARPENTER_PROFILE = "carpenter_profile"
    const val HOME = "home"
    const val PORTFOLIO = "projects"
    const val MORE = "more"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val CREATE_PORTFOLIO_ITEM = "create_portfolio_item"
    const val EDIT_PORTFOLIO_ITEM = "edit_portfolio_item"
    const val PORTFOLIO_ITEM_DETAIL = "portfolio_item_detail"
    const val CARPENTER_PUBLIC_PROFILE = "carpenter_public_profile"
    const val PEDIDOS = "pedidos"
    const val EDIT_PEDIDO = "edit_pedido"
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
    val tieneCarpenterProfile by mainViewModel.tieneCarpenterProfile.collectAsStateWithLifecycle()

    // Auto-navegación: si el usuario está logueado pero no completó el perfil,
    // lo mandamos a Completar Perfil (solo si NO estamos en pantallas de auth).
    LaunchedEffect(currentUser, tieneCarpenterProfile) {
        val user = currentUser ?: return@LaunchedEffect
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // Rutas de autenticación donde NO hay que redirigir
        val rutasQueNoRedirigen = listOf(
            Routes.WELCOME_1,
            Routes.WELCOME_2,
            Routes.LOGIN,
            Routes.REGISTER,
            Routes.REGISTER_CREDENTIALS,
            Routes.EDIT_PROFILE,
            Routes.CARPENTER_PROFILE
        )

        // Caso 1: perfil básico incompleto → COMPLETE_PROFILE
        if (!user.profileCompleted && currentRoute !in rutasQueNoRedirigen) {
            navController.navigate(Routes.COMPLETE_PROFILE) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }

        // Caso 2: carpintero sin perfil de taller → CARPENTER_PROFILE
        if (user.role == UserRole.CARPENTER &&
            tieneCarpenterProfile == false &&
            currentRoute !in rutasQueNoRedirigen
        ) {
            navController.navigate(Routes.CARPENTER_PROFILE) {
                popUpTo(0) { inclusive = true }
            }
        }

        // Caso 3: usuario logueado y estamos en WELCOME → ir al destino correcto
        // (para sesión persistente: cierra la app y al volver va directo)

        if (currentRoute == Routes.WELCOME_1 || currentRoute == Routes.WELCOME_2) {
            if (user.profileCompleted) {
                val destination = if (
                    user.role == UserRole.CARPENTER &&
                    tieneCarpenterProfile == false
                ) {
                    Routes.CARPENTER_PROFILE
                } else {
                    Routes.HOME
                }
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            }
            return@LaunchedEffect
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
                onLoginSuccess = { user ->
                    // Navegamos a HOME. El LaunchedEffect(currentUser, tieneCarpenterProfile)
                    // va a redirigir si el perfil está incompleto.
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
            val completeProfileViewModel = remember { completeProfileGoogleViewModel() }

            // Inicializamos con los datos del usuario actual (de Google)
            LaunchedEffect(currentUser) {
                currentUser?.let { completeProfileViewModel.initialize(it) }
            }
            CompleteProfileScreen(
                viewModel = completeProfileViewModel,
                onBack = {
                    // Cerrar sesión y volver al Welcome
                    navController.navigate(Routes.WELCOME_1) {
                        popUpTo(0) { inclusive = true }
                    }
                },
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
        composable(
            route = "${Routes.CARPENTER_PROFILE}?fromEdit={fromEdit}",
            arguments = listOf(
                navArgument("fromEdit") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromEdit = backStackEntry.arguments?.getBoolean("fromEdit") ?: false

            val carpenterViewModel = remember { completeProfileTallerViewModel() }

            LaunchedEffect(currentUser) {
                currentUser?.let { carpenterViewModel.initialize(it.uid) }
            }

            CarpenterProfileScreen(
                viewModel = carpenterViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    if (fromEdit) {
                        // Vino desde "Más" o "Perfil" → volver atrás
                        navController.popBackStack()
                    } else {
                        // Vino del registro → ir al Home
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- PERFIL PÚBLICO DEL CARPINTERO (vista del cliente) ---
        composable(
            route = "${Routes.CARPENTER_PUBLIC_PROFILE}/{uid}",
            arguments = listOf(
                navArgument("uid") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""

            val publicProfileViewModel = remember {
                CarpenterPublicProfileViewModel()
            }

            CarpenterPublicProfileScreen(
                viewModel = publicProfileViewModel,
                uid = uid,
                onBack = { navController.popBackStack() }
            )
        }

        // --- HOME ---
        composable(Routes.HOME) {
            val homeViewModel = remember { HomeViewModel() }

            MainScaffold(
                currentRoute = Routes.HOME,
                navController = navController
            ) {

                HomeScreen(
                    viewModel = homeViewModel,
                    onCarpenterClick = { uid -> navController.navigate("${Routes.CARPENTER_PUBLIC_PROFILE}/$uid") },
                    onPortfolioItemClick = { itemId -> navController.navigate("${Routes.EDIT_PORTFOLIO_ITEM}?itemId=$itemId") },
                    onSeeAllCarpentersClick = { navController.navigate(Routes.PORTFOLIO) },
                    onSeeAllPortfolioClick = { navController.navigate(Routes.PORTFOLIO) },
                    onOrderClick = { pedidoId -> navController.navigate("${Routes.EDIT_PEDIDO}?pedidoId=$pedidoId") },
                    onSeeAllOrdersClick = { navController.navigate(Routes.PEDIDOS) }
                )
            }
        }

        // --- PROJECTS ---

        composable(Routes.PORTFOLIO) {
            val portfolioViewModel = remember { PortfolioViewModel() }
            MainScaffold(
                currentRoute = Routes.PORTFOLIO,
                navController = navController
            ) {
                PortfolioScreen(
                    viewModel = portfolioViewModel,
                    onCreateClick = {
                        navController.navigate(Routes.CREATE_PORTFOLIO_ITEM)
                    },
                    onItemClick = { item ->
                        navController.navigate("${Routes.PORTFOLIO_ITEM_DETAIL}/${item.id}")  // ✅
                    }
                )
            }
        }

        // --- CREAR TRABAJO ---
        composable(Routes.CREATE_PORTFOLIO_ITEM) {
            val createViewModel = remember { CreatePortfolioViewModel() }
            CreatePortfolioScreen(
                viewModel = createViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // --- EDITAR TRABAJO ---
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
                EditPortfolioViewModel().apply {
                    if (itemId != null) {
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


        // --- DETALLE DE TRABAJO ---
    composable(
        route = "${Routes.PORTFOLIO_ITEM_DETAIL}/{itemId}",
        arguments = listOf(
            navArgument("itemId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
        val detailViewModel = remember { DetailPortfolioViewModel() }

        DetailPortfolioScreen(
            viewModel = detailViewModel,
            itemId = itemId,
            onBack = { navController.popBackStack() },
            onEditClick = { id ->
                navController.navigate("${Routes.EDIT_PORTFOLIO_ITEM}?itemId=$id")
            },
            onDeleteSuccess = {
                navController.popBackStack()
            }
        )
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
                    onEditCarpenterProfile = {
                        navController.navigate("${Routes.CARPENTER_PROFILE}?fromEdit=true")
                    },
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
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                    onEditCarpenterProfile = {
                        navController.navigate("${Routes.CARPENTER_PROFILE}?fromEdit=true")
                    }
                )
            }
        }

        // --- PEDIDOS (Centro de Pedidos) ---
        composable(Routes.PEDIDOS) {
            val pedidosViewModel = remember { PedidosViewModel() }

            MainScaffold(
                currentRoute = Routes.PEDIDOS,
                navController = navController
            ) {
                PedidosScreen(
                    viewModel = pedidosViewModel,
                    onCreateClick = {
                        navController.navigate(Routes.EDIT_PEDIDO)
                    },
                    onEditClick = { pedido ->
                        navController.navigate("${Routes.EDIT_PEDIDO}?pedidoId=${pedido.id}")
                    }
                )
            }
        }

        // --- CREAR/EDITAR PEDIDO ---
        composable(
            route = "${Routes.EDIT_PEDIDO}?pedidoId={pedidoId}",
            arguments = listOf(
                navArgument("pedidoId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId")

            val editPedidoViewModel = remember {
                EditPedidoViewModel().apply {
                    if (pedidoId.isNullOrBlank()) {
                        initializeCreate()
                    } else {
                        initializeEdit(pedidoId)
                    }
                }
            }

            EditPedidoScreen(
                viewModel = editPedidoViewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
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
        BottomNavItem(Routes.PORTFOLIO, "Proyectos", Icons.Filled.Folder),
        BottomNavItem(Routes.PEDIDOS, "Pedidos", Icons.Filled.Inbox),
        BottomNavItem(Routes.MORE, "Más", Icons.Filled.MoreHoriz),
        BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
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