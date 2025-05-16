package data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userLogin: String,
    val date: String,
    val time: String,
    val message: String
)