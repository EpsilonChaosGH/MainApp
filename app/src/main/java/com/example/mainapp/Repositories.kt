package com.example.mainapp

import android.content.Context
import androidx.room.Room
import com.example.mainapp.model.accounts.AccountsRepository
import com.example.mainapp.model.accounts.room.AccountsRepositoryImpl
import com.example.mainapp.model.elements.ElementsRepository
import com.example.mainapp.model.elements.room.RoomElementRepository
import com.example.mainapp.model.room.AppDatabase
import com.example.mainapp.model.settings.AppSettings
import com.example.mainapp.model.settings.SharedPreferencesAppSettings
import com.example.mainapp.utils.security.DefaultSecurityUtilsImpl
import com.example.mainapp.utils.security.SecurityUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


object Repositories {

    private lateinit var applicationContext: Context

    val securityUtils: SecurityUtils by lazy { DefaultSecurityUtilsImpl() }

    private val database: AppDatabase by lazy<AppDatabase> {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            .createFromAsset("init_db.db")
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }

    val accountsRepository: AccountsRepository by lazy {
        AccountsRepositoryImpl(database.getAccountsDao(), appSettings, ioDispatcher, securityUtils)
    }

    val elementsRepository: ElementsRepository by lazy {
        RoomElementRepository(database.getElementDao(), appSettings , ioDispatcher)
    }

    fun init(context: Context) {
        applicationContext = context
    }
}