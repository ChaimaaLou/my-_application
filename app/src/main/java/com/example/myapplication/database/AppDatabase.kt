package com.example.myapplication.database

import android.content.Context
import androidx.room.*
import com.example.myapplication.converters.DateTypeConverter
import com.example.myapplication.converters.JsonConverter
import com.example.myapplication.converters.ListConverter
import com.example.myapplication.entities.*


@Database(
    entities = [
        Culture::class,
        Exploitation::class,
        Parcelle::class,
        Saison::class,
        TypeSol::class,
        Variete::class,
        CultureVarieteCrossRef::class
    ],
    version = 10,
    exportSchema = true
)
@TypeConverters(DateTypeConverter::class,JsonConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun AppDao(): AppDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )   .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        ///////////////////////////////////////////////

    }
}