package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlQuranResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: List<AlQuranEditionData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AlQuranEditionData(
    @Json(name = "number") val number: Int,
    @Json(name = "name") val name: String,
    @Json(name = "englishName") val englishName: String,
    @Json(name = "englishNameTranslation") val englishNameTranslation: String,
    @Json(name = "revelationType") val revelationType: String,
    @Json(name = "numberOfAyahs") val numberOfAyahs: Int,
    @Json(name = "ayahs") val ayahs: List<AlQuranAyahItem> = emptyList(),
    @Json(name = "edition") val edition: AlQuranEditionInfo? = null
)

@JsonClass(generateAdapter = true)
data class AlQuranEditionInfo(
    @Json(name = "identifier") val identifier: String,
    @Json(name = "language") val language: String,
    @Json(name = "name") val name: String,
    @Json(name = "englishName") val englishName: String,
    @Json(name = "format") val format: String,
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class AlQuranAyahItem(
    @Json(name = "number") val number: Int,
    @Json(name = "text") val text: String,
    @Json(name = "numberInSurah") val numberInSurah: Int
)
