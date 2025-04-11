package data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.model.MachineStatusEntity

@Dao
interface MachineStatusDao {
    @Query("SELECT * FROM machine_statuses")
    suspend fun getAll(): List<MachineStatusEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statuses: List<MachineStatusEntity>)
}