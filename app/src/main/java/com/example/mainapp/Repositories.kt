package com.example.mainapp

import android.content.Context
import androidx.room.Room
import com.example.mainapp.model.accounts.AccountsRepository
import com.example.mainapp.model.accounts.AccountsRepositoryImpl
import com.example.mainapp.model.settings.AppSettings
import com.example.mainapp.model.settings.SharedPreferencesAppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


object Repositories {

    private lateinit var applicationContext: Context

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }

    val accountsRepository: AccountsRepository by lazy {
        AccountsRepositoryImpl( appSettings, ioDispatcher)
    }

    fun init(context: Context) {
        applicationContext = context
    }
}