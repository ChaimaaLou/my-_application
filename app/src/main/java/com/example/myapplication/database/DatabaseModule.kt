package com.example.myapplication.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    )= Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "my_database"
    )   .createFromAsset("database/parcelle.db")
        .build()

    @Singleton
    @Provides
    fun ProvideDao(database: AppDatabase)= database.AppDao()

}