package com.example.myapplication.database

import androidx.lifecycle.ViewModel
import com.example.myapplication.database.AppDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appDbRepository: AppDbRepository
): ViewModel() {
    val readAll= appDbRepository.readAll
}