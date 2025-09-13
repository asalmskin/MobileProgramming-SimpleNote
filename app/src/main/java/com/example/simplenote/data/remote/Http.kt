package com.example.simplenote.data.remote

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

val Context.dataStore by preferencesDataStore("auth")

class TokenStore(private val context: Context){
    private val accessKey = stringPreferencesKey("access")
    private val refreshKey = stringPreferencesKey("refresh")
    suspend fun save(access:String, refresh:String){ context.dataStore.edit { it[accessKey]=access; it[refreshKey]=refresh } }
    suspend fun access():String?{ return context.dataStore.data.first()[accessKey] }
    suspend fun refresh():String?{ return context.dataStore.data.first()[refreshKey] }
    suspend fun clear(){ context.dataStore.edit { it.clear() } }
}

class AuthInterceptor(private val context: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val access = runBlocking { TokenStore(context).access() }
        val b = req.newBuilder().addHeader("Accept","application/json")
        if(!access.isNullOrBlank()) b.addHeader("Authorization","Bearer $access")
        return chain.proceed(b.build())
    }
}

class TokenRefreshInterceptor(
    private val context: Context,
    private val baseUrl: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header("X-Retry") == "1") return chain.proceed(original)
        val first = chain.proceed(original)
        if (first.code != 401) return first
        first.close()
        val store = TokenStore(context)
        val r = runBlocking { store.refresh() } ?: return chain.proceed(original)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val media = "application/json".toMediaType()
        val body = """{"refresh":"$r"}""".toRequestBody(media)
        val refreshReq = Request.Builder()
            .url(baseUrl + "api/auth/token/refresh/")
            .post(body)
            .build()
        val tmpClient = OkHttpClient()
        val resp = tmpClient.newCall(refreshReq).execute()
        if (!resp.isSuccessful) return chain.proceed(original)
        val json = resp.body?.string().orEmpty()
        val token = moshi.adapter(TokenResponse::class.java).fromJson(json)
        val newAccess = token?.access ?: return chain.proceed(original)
        runBlocking { store.save(newAccess, r) }
        val retried = original.newBuilder()
            .header("Authorization","Bearer $newAccess")
            .header("X-Retry","1")
            .build()
        return chain.proceed(retried)
    }
}

fun apiService(context: Context, baseUrl:String): SimpleApi {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val client = OkHttpClient.Builder()
        .addInterceptor(TokenRefreshInterceptor(context, baseUrl))
        .addInterceptor(AuthInterceptor(context))
        .addInterceptor(logging)
        .build()
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SimpleApi::class.java)
}
