package data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.MachineStatus

@Entity(tableName = "machine_statuses")
data class MachineStatusEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val color: String
)

fun MachineStatusEntity.toDomain() = MachineStatus(
    id = id,
    name = name,
    color = Color(android.graphics.Color.parseColor(color))
)