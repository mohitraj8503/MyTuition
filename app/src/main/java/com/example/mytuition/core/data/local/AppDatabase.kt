package com.example.mytuition.core.data.local

import android.content.Context
import androidx.room.*

@Entity(tableName = "home_data_cache")
data class HomeDataCacheEntity(
    @PrimaryKey val date: String,
    val dataJson: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_feature_cache")
data class FeatureCacheEntity(
    @PrimaryKey val key: String,
    val dataJson: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Dao
interface HomeDataCacheDao {
    @Query("SELECT * FROM home_data_cache WHERE date = :date LIMIT 1")
    suspend fun getHomeData(date: String): HomeDataCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHomeData(entity: HomeDataCacheEntity)

    @Query("DELETE FROM home_data_cache")
    suspend fun clear()
}

@Dao
interface FeatureCacheDao {
    @Query("SELECT * FROM app_feature_cache WHERE key = :key LIMIT 1")
    suspend fun getCache(key: String): FeatureCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCache(entity: FeatureCacheEntity)

    @Query("DELETE FROM app_feature_cache WHERE key = :key")
    suspend fun clearKey(key: String)
}

@Database(entities = [HomeDataCacheEntity::class, FeatureCacheEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDataCacheDao(): HomeDataCacheDao
    abstract fun featureCacheDao(): FeatureCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mytuition_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
