package data.repository

import androidx.compose.ui.graphics.Color
import data.model.LogEntity
import data.remote.dto.MachineIdWrapper
import data.remote.dto.StateUpdateRequest
import data.remote.mesa.MesaApi
import domain.model.Machine
import domain.model.MachineStatus
import retrofit2.Response
import ui.components.ConnectionState

class MachineRepositoryRemote(private val api: MesaApi) : MachineRepository {
    override suspend fun getMachines(): List<Machine> =
        api.getAllMachines().machines.map { Machine(it.id, it.name, true) }

    override suspend fun getStatuses(): List<MachineStatus> {
        val codes = api.getAvailableStates().states
        return codes.mapIndexed { index, code ->
            MachineStatus(id = index, name = code, color = Color.Red)
        }
    }

    suspend fun selectMachine(machine: Machine): Boolean {
        val body = mapOf("current_machine" to MachineIdWrapper(machine.id))
        val response: Response<Unit> = api.selectMachine(body)
        return if (response.isSuccessful) {
            // здесь можно обновить какое‑то внутреннее состояние
            true
        } else {
            // например, логировать ошибку или кидать своё исключение
            false
        }
    }

    suspend fun updateState(request: StateUpdateRequest): Boolean {
        val response = api.updateState(request)
        return response.isSuccessful
    }


    override suspend fun logStatus(log: LogEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getLogsForUser(login: String): List<LogEntity> {
        TODO("Not yet implemented")
    }


    override suspend fun deleteLogsForUser(login: String) {
        TODO("Not yet implemented")
    }

    override suspend fun checkConnection(): ConnectionState =
        ConnectionState.Connected

}