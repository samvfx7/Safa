package com.example.data.remote

import com.example.data.remote.model.AlQuranResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AlQuranCloudService {

    @GET("v1/surah/{surahNumber}/editions/quran-uthmani,en.sahih,en.transliteration")
    suspend fun getSurahEditions(
        @Path("surahNumber") surahNumber: Int
    ): AlQuranResponse
}
