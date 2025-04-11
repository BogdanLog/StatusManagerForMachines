package data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.model.MachineEntity

@Dao
interface MachineDao {
    @Query("SELECT * FROM machines")
    suspend fun getAll(): List<MachineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(machines: List<MachineEntity>)
}