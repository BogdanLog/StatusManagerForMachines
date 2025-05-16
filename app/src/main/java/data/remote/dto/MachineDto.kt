package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MachineDto(
    @SerialName("machine_id") val id: Int,
    @SerialName("machine_name") val name: String
)