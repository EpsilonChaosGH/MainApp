package com.example.mainapp.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mainapp.model.accounts.room.AccountsDao
import com.example.mainapp.model.accounts.room.entities.AccountDbEntity

@Database(
    version = 1,
    entities = [
        AccountDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAccountsDao(): AccountsDao

}