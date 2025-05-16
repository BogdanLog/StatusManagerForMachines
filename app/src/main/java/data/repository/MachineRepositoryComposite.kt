package data.repository

import data.model.LogEntity
import data.remote.dto.StateUpdateRequest
import ui.components.ConnectionState

class MachineRepositoryComposite(
    private val remote: MachineRepositoryRemote,
    private val local: MachineRepositoryImpl
) : MachineRepository {
    override suspend fun getMachines() = remote.getMachines()
    override suspend fun getStatuses() = remote.getStatuses()
    override suspend fun logStatus(log: LogEntity) = local.logStatus(log)
    override suspend fun getLogsForUser(login: String) = local.getLogsForUser(login)
    override suspend fun deleteLogsForUser(login: String) = local.deleteLogsForUser(login)
    override suspend fun checkConnection(): ConnectionState = remote.checkConnection()
    suspend fun updateState(request: StateUpdateRequest) = remote.updateState(request)
}