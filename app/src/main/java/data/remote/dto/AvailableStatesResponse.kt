package data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableStatesResponse(@SerialName("available_states") val states: List<String>)