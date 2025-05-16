package data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.Machine

@Entity(tableName = "machines")
data class MachineEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val isActive: Boolean
)

fun MachineEntity.toDomain() = Machine(
    id = id,
    name = name,
    isActive = isActive
)