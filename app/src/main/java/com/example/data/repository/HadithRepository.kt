package com.example.data.repository

import com.example.data.local.dao.HadithDao
import com.example.data.local.entity.HadithEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class HadithRepository(
    private val hadithDao: HadithDao
) {
    val collections = listOf(
        "All",
        "40 Hadith Nawawi",
        "Sahih Bukhari",
        "Sahih Muslim",
        "Sunan Abi Dawud",
        "Jami` at-Tirmidhi",
        "Riyad as-Salihin"
    )

    fun getAllHadiths(): Flow<List<HadithEntity>> = hadithDao.getAllHadiths()

    fun getHadithsByCollection(collection: String): Flow<List<HadithEntity>> {
        return if (collection == "All") {
            hadithDao.getAllHadiths()
        } else {
            hadithDao.getHadithsByCollection(collection)
        }
    }

    fun getFavoriteHadiths(): Flow<List<HadithEntity>> = hadithDao.getFavoriteHadiths()

    fun searchHadiths(query: String): Flow<List<HadithEntity>> = hadithDao.searchHadiths(query)

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        hadithDao.updateFavorite(id, isFavorite)
    }

    suspend fun preloadHadithsIfNeeded() = withContext(Dispatchers.IO) {
        val existing = hadithDao.getAllHadiths().firstOrNull()
        if (existing.isNullOrEmpty() || existing.size < HadithData.allHadiths.size) {
            hadithDao.insertHadiths(HadithData.allHadiths)
        }
    }

    fun getHadithOfTheDay(): HadithEntity {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = dayOfYear % HadithData.allHadiths.size
        return HadithData.allHadiths[index]
    }

    val initialHadithList: List<HadithEntity> = HadithData.allHadiths
}
