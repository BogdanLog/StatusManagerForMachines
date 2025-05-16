package ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.LogEntity
import data.remote.dto.StateUpdateRequest
import data.repository.MachineRepository
import data.repository.MachineRepositoryComposite
import data.repository.MachineRepositoryRemote
import domain.model.Machine
import domain.model.MachineStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import ui.components.ConnectionState

data class MainViewState(
    val machines: List<Machine> = emptyList(),
    val selectedMachine: Machine? = null,
    val machineMenuExpanded: Boolean = false,
    val statuses: List<MachineStatus> = emptyList(),
    val selectedStatus: MachineStatus? = null,
    val statusMenuExpanded: Boolean = false,
    val elapsedTime: Long = 0L,
    val isStartEnabled: Boolean = false,
    val isStopEnabled: Boolean = false,
    val isInputEnabled: Boolean = true,
    val connectionState: ConnectionState = ConnectionState.Connecting,
    val taskNumber: String = "",
    val comment: String = "",
    val logs: List<String> = emptyList()
)

class MainViewModel(
    private val repository: MachineRepositoryComposite
) : ViewModel() {

    private val _state = MutableStateFlow(MainViewState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    private fun getCurrentTime(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private fun getCurrentDate(): String =
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    private fun formatLogs(logEntities: List<LogEntity>): List<String> {
        val result = mutableListOf<String>()
        var lastDate: String? = null
        for (log in logEntities) {
            if (log.date != lastDate) {
                lastDate = log.date
                result.add("Дата: ${log.date}\n")
            }
            result.add(log.message)
        }
        return result
    }

    fun clearInputs() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                selectedMachine = null,
                machineMenuExpanded = false,
                selectedStatus = null,
                statusMenuExpanded = false,
                taskNumber = "",
                comment = "",
                elapsedTime = 0L,
                isStartEnabled = false,
                isStopEnabled = false,
                isInputEnabled = true,
                logs = emptyList()
            )
        }
    }

    fun startTimer(userLogin: String) {
        val time = getCurrentTime()
        val date = getCurrentDate()
        val machine = state.value.selectedMachine
        val status = state.value.selectedStatus
        val taskNo = state.value.taskNumber
        val comment = state.value.comment

        if (machine != null && status != null) {
            val message =
                "В $time у машины \"${machine.name}\" был поставлен статус \"${status.name}\" с комментарием \"$comment\" под номером задачи $taskNo\n"

            viewModelScope.launch {
                repository.logStatus(
                    LogEntity(
                        userLogin = userLogin,
                        date = date,
                        time = time,
                        message = message
                    )
                )
                val persisted = repository.getLogsForUser(userLogin)
                _state.update { it.copy(logs = formatLogs(persisted)) }
            }
        }

        _state.update {
            it.copy(
                isStartEnabled = false,
                isStopEnabled = true,
                isInputEnabled = false
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _state.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }

    fun stopTimer(userLogin: String) {
        val time = getCurrentTime()
        val date = getCurrentDate()
        val machine = state.value.selectedMachine
        val status = state.value.selectedStatus

        if (machine != null && status != null) {
            val message =
                "В $time у машины \"${machine.name}\" статус \"${status.name}\" был завершен\n"

            viewModelScope.launch {
                repository.updateState(
                    StateUpdateRequest(
                        machineState = state.value.selectedStatus!!.name,
                        description = state.value.comment
                    )
                )
                repository.logStatus(
                    LogEntity(
                        userLogin = userLogin,
                        date = date,
                        time = time,
                        message = message
                    )
                )
                val persisted = repository.getLogsForUser(userLogin)
                _state.update { it.copy(logs = formatLogs(persisted)) }
                val body = StateUpdateRequest(
                    machineState = status.name,
                    description = "Завершено пользователем $userLogin"
                )
                repository.updateState(body)
            }
        }

        timerJob?.cancel()
        _state.update {
            it.copy(
                elapsedTime = 0L,
                isStartEnabled = true,
                isStopEnabled = false,
                isInputEnabled = true
            )
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val machines = repository.getMachines()
            val statuses = repository.getStatuses()
            val connection = repository.checkConnection()
            _state.update {
                it.copy(
                    machines = machines,
                    statuses = statuses,
                    connectionState = connection
                )
            }
        }
    }

    fun loadLogsFor(userLogin: String) {
        viewModelScope.launch {
            val persisted = repository.getLogsForUser(userLogin)
                .map { "${it.date}: ${it.message}" }
            _state.update { it.copy(logs = persisted) }
        }
    }

    fun deleteAllLogs(userLogin: String) {
        viewModelScope.launch {
            repository.deleteLogsForUser(userLogin)
            _state.update { it.copy(logs = emptyList()) }
        }
    }

    fun toggleMachineMenu() {
        _state.update {
            it.copy(
                machineMenuExpanded = !it.machineMenuExpanded
            )
        }
    }

    fun closeMachineMenu() {
        _state.update {
            it.copy(machineMenuExpanded = false)
        }
    }

    fun selectMachine(machine: Machine) {
        viewModelScope.launch {
            // отправляем выбор машины на сервер
            if ((repository as? MachineRepositoryRemote)?.selectMachine(machine) != false) {
                _state.update { it.copy(selectedMachine = machine) }
                updateStartButtonState()
            }
        }
    }

    fun toggleStatusMenu() {
        _state.update {
            it.copy(statusMenuExpanded = !it.statusMenuExpanded)
        }
    }

    fun closeStatusMenu() {
        _state.update {
            it.copy(statusMenuExpanded = false)
        }
    }

    fun selectStatus(status: MachineStatus) {
        _state.update {
            it.copy(selectedStatus = status)
        }
        updateStartButtonState()
    }

    fun onTaskNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() } && (newValue.toLongOrNull() ?: 0) <= Int.MAX_VALUE) {
            _state.update {
                it.copy(taskNumber = newValue)
            }
            updateStartButtonState()
        }
    }

    fun onCommentChange(newValue: String) {
        _state.update {
            it.copy(comment = newValue)
        }
        updateStartButtonState()
    }

    private fun updateStartButtonState() {
        val current = state.value
        val enable = current.selectedMachine != null &&
                current.selectedStatus != null &&
                current.taskNumber.isNotBlank() &&
                (current.taskNumber != "0" || current.comment.isNotBlank())
        _state.update { it.copy(isStartEnabled = enable) }
    }
}