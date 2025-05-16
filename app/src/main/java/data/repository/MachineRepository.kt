package data.repository

import data.model.LogEntity
import domain.model.Machine
import domain.model.MachineStatus
import ui.components.ConnectionState

interface MachineRepository {
    suspend fun getMachines(): List<Machine>
    suspend fun getStatuses(): List<MachineStatus>
    suspend fun logStatus(log: LogEntity)
    suspend fun getLogsForUser(login: String): List<LogEntity>
    suspend fun checkConnection(): ConnectionState
    suspend fun deleteLogsForUser(login: String)
}