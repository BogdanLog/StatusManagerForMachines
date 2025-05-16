package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MachineIdWrapper(@SerialName("machine_id") val id: Int)