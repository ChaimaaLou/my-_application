package com.example.myapplication.database

import com.example.myapplication.database.AppDao
import javax.inject.Inject

class AppDbRepository @Inject constructor(
    private val appDao: AppDao
) {
    val readAll = appDao.readParcelle()
}