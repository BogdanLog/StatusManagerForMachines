package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StateUpdateRequest(
    @SerialName("machine_state") val machineState: String,
    val description: String? = null
)