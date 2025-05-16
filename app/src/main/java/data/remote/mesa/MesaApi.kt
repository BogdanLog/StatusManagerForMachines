package data.remote.mesa

import data.remote.dto.AvailableStatesResponse
import data.remote.dto.CurrentMachineResponse
import data.remote.dto.LoginRequest
import data.remote.dto.LoginResponse
import data.remote.dto.MachineIdWrapper
import data.remote.dto.MachineStateResponse
import data.remote.dto.MachinesResponse
import data.remote.dto.StateUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface MesaApi {
    @POST("/api/o/auth")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("/api/logout")
    suspend fun logout(): Response<Unit>

    @GET("/api/machine/all")
    suspend fun getAllMachines(): MachinesResponse

    @GET("/api/machine/current")
    suspend fun getCurrentMachine(): CurrentMachineResponse

    @PUT("/api/machine/current")
    suspend fun selectMachine(@Body body: Map<String, MachineIdWrapper>): Response<Unit>

    @GET("/api/machine/available/states")
    suspend fun getAvailableStates(): AvailableStatesResponse

    @GET("/api/machine/current/state")
    suspend fun getCurrentState(): MachineStateResponse

    @PUT("/api/machine/current/state")
    suspend fun updateState(@Body body: StateUpdateRequest): Response<Unit>
}