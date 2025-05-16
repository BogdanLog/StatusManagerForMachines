package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MachineStateResponse(@SerialName("machine_state") val state: String)