package com.example.statusmanagerformachines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.statusmanagerformachines.ui.theme.StatusManagerForMachinesTheme
import com.example.statusmanagerformachines.ui.theme.ViewModelFactory
import data.db.AppDatabase
import data.prefs.ThemeOption
import data.prefs.ThemePrefs
import data.prefs.UserPrefs
import data.remote.retrofit.RetrofitInstance
import data.remote.retrofit.TokenPrefs
import data.repository.AuthRepositoryRemote
import data.repository.MachineRepositoryComposite
import data.repository.MachineRepositoryImpl
import data.repository.MachineRepositoryRemote
import ui.auth.AuthScreen
import ui.auth.AuthState
import ui.auth.AuthViewModel
import ui.auth.AuthViewModelFactory
import ui.main.MainScreen
import ui.main.MainViewModel

class MainActivity : ComponentActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }

    private val mainVm by viewModels<MainViewModel> {
        ViewModelFactory(
            MachineRepositoryComposite(
                remote = MachineRepositoryRemote(RetrofitInstance.api),
                local = MachineRepositoryImpl(db)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitInstance.init(this)
        val themePrefs = ThemePrefs(applicationContext)
        setContent {
            val themeOption by themePrefs.themeOptionFlow.collectAsState(initial = ThemeOption.System)
            val userPrefs = UserPrefs(applicationContext)
            val authVm: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(
                    AuthRepositoryRemote(
                        RetrofitInstance.api,
                        TokenPrefs(applicationContext)
                    ),
                    userPrefs
                )
            )

            StatusManagerForMachinesTheme(themeOption = themeOption) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            val authState by authVm.state.collectAsState()
                            val isAuthorized = authState is AuthState.Authenticated
                            val userLogin = (authState as? AuthState.Authenticated)?.login.orEmpty()
                            MainScreen(
                                viewModel = mainVm,
                                onProfileClick = { navController.navigate("profile") },
                                onAuthClick = { navController.navigate("login") },
                                onLogout = { authVm.logout() },
                                isAuthorized = isAuthorized,
                                userLogin = userLogin
                            )
                        }
                        composable("login") {
                            AuthScreen(
                                vm = authVm,
                                onDone = {
                                    navController.popBackStack("main", inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}