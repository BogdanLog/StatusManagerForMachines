package ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.MachineRepository
import domain.model.Machine
import domain.model.MachineStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.components.ConnectionState

// Data class для хранения состояния UI
data class MainViewState(
    val machines: List<Machine> = emptyList(),
    val selectedMachine: Machine? = null,
    val machineMenuExpanded: Boolean = false,
    val statuses: List<MachineStatus> = emptyList(),
    val selectedStatus: MachineStatus? = null,
    val statusMenuExpanded: Boolean = false,
    val elapsedTime: Long = 0L,
    val isStartEnabled: Boolean = true,
    val isStopEnabled: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.Connecting
)

class MainViewModel(
    private val repository: MachineRepository
) : ViewModel() {
    // Основное состояние приложения
    private val _state = MutableStateFlow(MainViewState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val machines = repository.getMachines()
            val statuses = repository.getStatuses()
            val connectionStatus = repository.checkConnection()
            _state.update { currentState ->
                currentState.copy(
                    machines = machines,
                    statuses = statuses,
                    connectionState = connectionStatus
                )
            }
        }
    }

    // Управление таймером
    fun startTimer() {
        timerJob?.cancel()
        _state.update { currentState ->
            currentState.copy(
                isStartEnabled = false,
                isStopEnabled = true
            )
        }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _state.update { currentState ->
                    currentState.copy(elapsedTime = currentState.elapsedTime + 1)
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _state.update { currentState ->
            currentState.copy(
                elapsedTime = 0L,
                isStartEnabled = true,
                isStopEnabled = false
            )
        }
    }

    // Управление меню выбора машины
    fun toggleMachineMenu() {
        _state.update { it.copy(machineMenuExpanded = !it.machineMenuExpanded) }
    }

    fun closeMachineMenu() {
        _state.update { it.copy(machineMenuExpanded = false) }
    }

    fun selectMachine(machine: Machine) {
        _state.update { it.copy(selectedMachine = machine) }
    }

    // Управление меню выбора статуса машины
    fun toggleStatusMenu() {
        _state.update { it.copy(statusMenuExpanded = !it.statusMenuExpanded) }
    }

    fun closeStatusMenu() {
        _state.update { it.copy(statusMenuExpanded = false) }
    }

    fun selectStatus(status: MachineStatus) {
        _state.update { it.copy(selectedStatus = status) }
    }
}