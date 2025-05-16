package ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.statusmanagerformachines.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import data.prefs.ThemeOption
import data.prefs.ThemePrefs
import kotlinx.coroutines.launch
import ui.components.ConnectionStatus
import ui.components.PortraitCaptureActivity
import ui.components.TimerDisplay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onProfileClick: () -> Unit,
    onAuthClick: () -> Unit,
    onLogout: () -> Unit,
    isAuthorized: Boolean,
    userLogin: String
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLogOutConfirm by remember { mutableStateOf(false) }
    var themeMenuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val themePrefs = remember { ThemePrefs(context) }
    val currentTheme by themePrefs.themeOptionFlow.collectAsState(initial = ThemeOption.System)
    val state by viewModel.state.collectAsState()
    val logsList = state.logs

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer ->
                logsList.forEach { line ->
                    writer.append(line)
                    writer.newLine()
                }
            }
        }
    }

    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            viewModel.onTaskNumberChange(result.contents)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(userLogin) {
        if (isAuthorized) {
            viewModel.loadLogsFor(userLogin)
        } else {
            viewModel.clearInputs()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatus(state.connectionState)
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAuthorized) userLogin else "Нет аккаунта",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            IconButton(onClick = {}, enabled = isAuthorized) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            Box(modifier = Modifier.wrapContentSize(Alignment.TopCenter)) {
                IconButton(
                    onClick = {
                        if (isAuthorized) showProfileDialog = true else onAuthClick()
                    }
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }

                if (showProfileDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnClickOutside = false,
                            dismissOnBackPress = true
                        ),
                        title = {
                            Box(Modifier.fillMaxWidth()) {
                                Text(
                                    "Профиль",
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(
                                    onClick = { showProfileDialog = false },
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(userLogin, modifier = Modifier.weight(1f))
                                    TextButton(
                                        onClick = { showLogOutConfirm = true },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                    ) {
                                        Text("Выйти")
                                    }
                                }

                                ExposedDropdownMenuBox(
                                    expanded = state.machineMenuExpanded,
                                    onExpandedChange = { viewModel.toggleMachineMenu() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = state.selectedMachine?.name ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Машина") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(state.machineMenuExpanded)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        enabled = isAuthorized && state.isInputEnabled
                                    )
                                    ExposedDropdownMenu(
                                        expanded = state.machineMenuExpanded,
                                        onDismissRequest = { viewModel.closeMachineMenu() }
                                    ) {
                                        state.machines.forEach { machine ->
                                            DropdownMenuItem(
                                                enabled = isAuthorized && state.isInputEnabled,
                                                text = { Text(machine.name) },
                                                onClick = {
                                                    viewModel.selectMachine(machine)
                                                    viewModel.closeMachineMenu()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }

                if (showLogOutConfirm) {
                    AlertDialog(
                        onDismissRequest = { showLogOutConfirm = false },
                        title = {
                            Text("Выйти из аккаунта")
                        },
                        text = {
                            Text("Вы действительно хотите выйти из аккаунта?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showLogOutConfirm = false
                                showProfileDialog = false
                                onLogout()
                            }) {
                                Text("Да, выйти")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogOutConfirm = false }) { Text("Отмена") }
                        }
                    )
                }
            }
            IconButton(
                onClick = { showSettingsDialog = true },
                enabled = isAuthorized
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }

            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    properties = DialogProperties(
                        dismissOnClickOutside = false,
                        dismissOnBackPress = true
                    ),
                    title = {
                        Box(Modifier.fillMaxWidth()) {
                            Text(
                                "Настройки",
                                modifier = Modifier
                                    .align(Alignment.CenterStart),
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(
                                onClick = { showSettingsDialog = false },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    },
                    text = {
                        Column {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Тема", Modifier.weight(1f))
                                val themeLabel = when (currentTheme) {
                                    ThemeOption.Light -> "Светлая тема"
                                    ThemeOption.Dark -> "Тёмная тема"
                                    ThemeOption.System -> "Как в системе"
                                }
                                Box {
                                    Text(
                                        themeLabel,
                                        Modifier
                                            .clickable { themeMenuExpanded = true }
                                            .padding(8.dp)
                                    )
                                    DropdownMenu(
                                        expanded = themeMenuExpanded,
                                        onDismissRequest = { themeMenuExpanded = false }
                                    ) {
                                        listOf(
                                            ThemeOption.Light,
                                            ThemeOption.Dark,
                                            ThemeOption.System
                                        ).forEach { option ->
                                            val optionLabel = when (option) {
                                                ThemeOption.Light -> "Светлая тема"
                                                ThemeOption.Dark -> "Тёмная тема"
                                                ThemeOption.System -> "Как в системе"
                                            }
                                            DropdownMenuItem(
                                                text = { Text(optionLabel) },
                                                onClick = {
                                                    scope.launch {
                                                        themePrefs.saveTheme(option)
                                                    }
                                                    themeMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Скачать журнал событий", Modifier.weight(1f))
                                TextButton(
                                    onClick = {
                                        val date = SimpleDateFormat(
                                            "dd.MM.yyyy",
                                            Locale.getDefault()
                                        ).format(Date())
                                        val filename =
                                            "История статусов пользователя $userLogin на $date.txt"
                                        createDocumentLauncher.launch(filename)
                                    }
                                ) {
                                    Text("Скачать")
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Очистить журнал событий", Modifier.weight(1f))
                                TextButton(
                                    onClick = { showDeleteConfirm = true },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                ) {
                                    Text("Удалить")
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Очистить журнал событий") },
                    text = { Text("Вы действительно хотите очистить журнал событий?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteAllLogs(userLogin)
                            showDeleteConfirm = false
                            showSettingsDialog = false
                        }) { Text("Да, очистить") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) { Text("Отмена") }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Выбранная машина: ${state.selectedMachine?.name ?: "Пусто"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = state.statusMenuExpanded,
            onExpandedChange = { viewModel.toggleStatusMenu() },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.selectedStatus?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Состояние машины") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(state.statusMenuExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                enabled = isAuthorized && state.isInputEnabled
            )
            ExposedDropdownMenu(
                expanded = state.statusMenuExpanded,
                onDismissRequest = { viewModel.closeStatusMenu() }
            ) {
                state.statuses.forEach { status ->
                    DropdownMenuItem(
                        enabled = isAuthorized && state.isInputEnabled,
                        text = { Text(status.name) },
                        onClick = {
                            viewModel.selectStatus(status)
                            viewModel.closeStatusMenu()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.taskNumber,
            onValueChange = { viewModel.onTaskNumberChange(it) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Номер задачи") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isAuthorized && state.isInputEnabled,
            trailingIcon = {
                IconButton(onClick = {
                    val options = ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setPrompt("Наведите камеру на QR‑код")
                        setBeepEnabled(true)
                        setOrientationLocked(true)
                        setCaptureActivity(PortraitCaptureActivity::class.java)
                    }
                    qrLauncher.launch(options)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_code),
                        contentDescription = "Сканировать QR"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.comment,
            onValueChange = { viewModel.onCommentChange(it) },
            label = { Text("Комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            enabled = isAuthorized && state.isInputEnabled
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.startTimer(userLogin) },
                enabled = isAuthorized && state.isStartEnabled,
                modifier = Modifier.size(114.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = if (state.isStartEnabled) Color.Gray else Color.DarkGray
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play_button),
                    contentDescription = "Старт",
                    tint = if (state.isStartEnabled) Color.Green else Color.LightGray,
                    modifier = Modifier
                        .size(46.dp)
                        .padding(start = 5.dp)
                )
            }

            Spacer(modifier = Modifier.width(48.dp))

            Button(
                onClick = { viewModel.stopTimer(userLogin) },
                enabled = isAuthorized && state.isStopEnabled,
                modifier = Modifier.size(114.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = if (state.isStopEnabled) Color.Gray else Color.DarkGray
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_stop_button),
                    contentDescription = "Стоп",
                    tint = if (state.isStopEnabled) Color.Red else Color.LightGray,
                    modifier = Modifier
                        .size(46.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TimerDisplay(state.elapsedTime)
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(top = 10.dp)
        ) {
            items(state.logs) { log ->
                Text(
                    text = log,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}