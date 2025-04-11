package data.repository

import domain.model.Machine
import domain.model.MachineStatus
import ui.components.ConnectionState

interface MachineRepository {
    suspend fun getMachines(): List<Machine>
    suspend fun getStatuses(): List<MachineStatus>
    suspend fun logStatus(
        machineId: Int,
        statusId: Int,
        taskNumber: String,
        comment: String,
        duration: Long
    )
    suspend fun checkConnection(): ConnectionState
}