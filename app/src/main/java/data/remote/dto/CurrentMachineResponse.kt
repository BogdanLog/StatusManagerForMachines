package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentMachineResponse(@SerialName("current_machine") val machine: MachineDto)