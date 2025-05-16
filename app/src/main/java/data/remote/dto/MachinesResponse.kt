package data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MachinesResponse(val machines: List<MachineDto>)