package data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.model.LogEntity

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: LogEntity)

    @Query("SELECT * FROM user_logs WHERE userLogin = :login ORDER BY id ASC")
    suspend fun getLogsForUser(login: String): List<LogEntity>

    @Query("DELETE FROM user_logs WHERE userLogin = :login")
    suspend fun deleteLogsForUser(login: String)
}