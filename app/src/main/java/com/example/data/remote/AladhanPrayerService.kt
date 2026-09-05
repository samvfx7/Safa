package com.example.data.remote

import com.example.data.remote.model.AladhanResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AladhanPrayerService {

    @GET("v1/timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 2, // 2 = ISNA, 1 = Karachi, 3 = MWL, 4 = Umm Al-Qura, 5 = Egyptian
        @Query("school") school: Int = 0 // 0 = Shafi, 1 = Hanafi
    ): AladhanResponse

    @GET("v1/timings/{timestamp}")
    suspend fun getTimingsByCoordinates(
        @Path("timestamp") timestamp: Long,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 2,
        @Query("school") school: Int = 0
    ): AladhanResponse
}

object ApiClient {
    private const val ALADHAN_BASE_URL = "https://api.aladhan.com/"
    private const val ALQURAN_BASE_URL = "https://api.alquran.cloud/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val aladhanService: AladhanPrayerService by lazy {
        Retrofit.Builder()
            .baseUrl(ALADHAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AladhanPrayerService::class.java)
    }

    val alQuranService: AlQuranCloudService by lazy {
        Retrofit.Builder()
            .baseUrl(ALQURAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AlQuranCloudService::class.java)
    }
}
