package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class GitHubUserResponse(
    @Json(name = "login") val login: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "public_repos") val publicRepos: Int? = 0,
    @Json(name = "followers") val followers: Int? = 0,
    @Json(name = "company") val company: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

data class GitHubEventRepo(
    @Json(name = "name") val name: String
)

data class GitHubEventResponse(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "repo") val repo: GitHubEventRepo?,
    @Json(name = "created_at") val createdAt: String
)

interface GitHubApiService {
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
        @Header("User-Agent") userAgent: String = "NexusPulse-App"
    ): GitHubUserResponse

    @GET("users/{username}/events/public")
    suspend fun getUserEvents(
        @Path("username") username: String,
        @Header("User-Agent") userAgent: String = "NexusPulse-App"
    ): List<GitHubEventResponse>

    companion object {
        @Volatile
        private var INSTANCE: GitHubApiService? = null

        fun create(): GitHubApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(8, TimeUnit.SECONDS)
                        .readTimeout(8, TimeUnit.SECONDS)
                        .connectionPool(okhttp3.ConnectionPool(2, 1, TimeUnit.MINUTES))
                        .build()

                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()

                    Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .client(client)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()
                        .create(GitHubApiService::class.java)
                        .also { INSTANCE = it }
                }
            }
        }
    }
}
