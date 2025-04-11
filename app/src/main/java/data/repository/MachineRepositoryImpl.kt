package data.repository

import data.db.AppDatabase
import data.model.toDomain
import domain.model.Machine
import domain.model.MachineStatus
import ui.components.ConnectionState

class MachineRepositoryImpl(
    private val db: AppDatabase
) : MachineRepository {
    override suspend fun getMachines(): List<Machine> {
        return db.machineDao().getAll().map { it.toDomain() }
    }

    override suspend fun getStatuses(): List<MachineStatus> {
        return db.machineStatusDao().getAll().map { it.toDomain() }
    }

    override suspend fun logStatus(
        machineId: Int,
        statusId: Int,
        taskNumber: String,
        comment: String,
        duration: Long
    ) {
        // реализация сохранения в базу
    }

    override suspend fun checkConnection(): ConnectionState {
        return ConnectionState.Connected // заглушка для локальной БД
    }
}