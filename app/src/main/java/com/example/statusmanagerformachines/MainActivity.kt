package com.example.statusmanagerformachines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ui.main.MainScreen
import com.example.statusmanagerformachines.ui.theme.StatusManagerForMachinesTheme
import com.example.statusmanagerformachines.ui.theme.ViewModelFactory
import data.db.AppDatabase
import data.repository.MachineRepositoryImpl
import ui.main.MainViewModel

class MainActivity : ComponentActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory(
            MachineRepositoryImpl(db)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StatusManagerForMachinesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}