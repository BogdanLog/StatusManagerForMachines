package ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.TimerDisplay
import domain.model.Machine
import ui.components.ConnectionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatus(state.connectionState)
            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = { /* Refresh */ },
                enabled = true
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            IconButton(
                onClick = { /* Settings */ },
                enabled = true
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        // выбор машины
        Text(
            text = "Машина",
            style =  MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        ExposedDropdownMenuBox(
            expanded = state.machineMenuExpanded,
            onExpandedChange = { viewModel.toggleMachineMenu() }
        ) {
            TextField(
                value = state.selectedMachine?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.machineMenuExpanded) }
            )
            ExposedDropdownMenu(
                expanded = state.machineMenuExpanded,
                onDismissRequest = { viewModel.closeMachineMenu() }
            ) {
                state.machines.forEach { machine: Machine ->
                    DropdownMenuItem(
                        text = { Text(machine.name) },
                        onClick = {
                            viewModel.selectMachine(machine)
                            viewModel.closeMachineMenu()
                        }
                    )
                }
            }
        }

        // выбор статуса машины
        Text(
            text = "Состояние машины",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        ExposedDropdownMenuBox(
            expanded = state.statusMenuExpanded,
            onExpandedChange = { viewModel.toggleStatusMenu() }
        ) {
            TextField(
                value = state.selectedStatus?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.statusMenuExpanded) }
            )
            ExposedDropdownMenu(
                expanded = state.statusMenuExpanded,
                onDismissRequest = { viewModel.closeStatusMenu() }
            ) {
                state.statuses.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.name) },
                        onClick = {
                            viewModel.selectStatus(status)
                            viewModel.closeStatusMenu()
                        }
                    )
                }
            }
        }

        // кнопки
        Row {
            Button(
                onClick = { viewModel.startTimer() },
                enabled = state.isStartEnabled
            ) {
                Text("Старт")
            }
            Spacer(Modifier.width(48.dp))
            Button(
                onClick = { viewModel.stopTimer() },
                enabled = state.isStopEnabled
            ) {
                Text("Стоп")
            }
        }

        // таймер
        TimerDisplay(state.elapsedTime)
    }
}