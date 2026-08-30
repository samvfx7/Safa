package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.DuaDao
import com.example.data.local.dao.HadithDao
import com.example.data.local.dao.PrayerDao
import com.example.data.local.dao.PrayerLogDao
import com.example.data.local.dao.QuranDao
import com.example.data.local.dao.TasbihDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.DuaEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.PrayerEntity
import com.example.data.local.entity.PrayerLogEntity
import com.example.data.local.entity.QuranAyahEntity
import com.example.data.local.entity.QuranSurahEntity
import com.example.data.local.entity.TasbihLogEntity

@Database(
    entities = [
        PrayerEntity::class,
        PrayerLogEntity::class,
        TasbihLogEntity::class,
        DuaEntity::class,
        HadithEntity::class,
        BookmarkEntity::class,
        QuranSurahEntity::class,
        QuranAyahEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NoorDatabase : RoomDatabase() {

    abstract fun prayerDao(): PrayerDao
    abstract fun prayerLogDao(): PrayerLogDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun duaDao(): DuaDao
    abstract fun hadithDao(): HadithDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: NoorDatabase? = null

        fun getDatabase(context: Context): NoorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorDatabase::class.java,
                    "noor_islamic_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
