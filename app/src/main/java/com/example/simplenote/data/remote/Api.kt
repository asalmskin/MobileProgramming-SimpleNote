package com.example.simplenote.data.remote

import com.squareup.moshi.Json
import retrofit2.http.*

data class TokenObtainPairRequest(val username: String, val password: String)
data class TokenResponse(val access: String, val refresh: String?)
data class TokenRefreshRequest(val refresh: String)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)
data class ChangePasswordRequest(val old_password: String, val new_password: String)
data class NoteDto(
    val id: Int,
    val title: String,
    val description: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)

data class NoteCreate(val title: String, val description: String)

data class Page<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
data class UserInfo(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)


interface SimpleApi {
    @GET("api/auth/userinfo/")
    suspend fun userinfo(): UserInfo

    @POST("api/auth/token/")
    suspend fun login(@Body req: TokenObtainPairRequest): TokenResponse

    @POST("api/auth/token/refresh/")
    suspend fun refresh(@Body req: TokenRefreshRequest): TokenResponse

    @POST("api/auth/register/")
    suspend fun register(@Body req: RegisterRequest): retrofit2.Response<Unit>


    @GET("api/notes/")
    suspend fun notes(@Query("page") page: Int?, @Query("page_size") pageSize: Int? = 10): Page<NoteDto>

    @GET("api/notes/filter")
    suspend fun notesFilter(
        @Query("title") title: String? = null,
        @Query("description") description: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = 10
    ): Page<NoteDto>

    @POST("api/notes/")
    suspend fun createNote(@Body body: NoteCreate): NoteDto

    @GET("api/notes/{id}/")
    suspend fun note(@Path("id") id: Int): NoteDto

    @PUT("api/notes/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: NoteCreate): NoteDto

    @DELETE("api/notes/{id}/")
    suspend fun delete(@Path("id") id: Int)

    @POST("api/auth/change-password/")
    suspend fun changePassword(@Body req: ChangePasswordRequest): Any
}
