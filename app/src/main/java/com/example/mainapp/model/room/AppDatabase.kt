package com.example.mainapp.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mainapp.model.accounts.room.AccountsDao
import com.example.mainapp.model.accounts.room.entities.AccountDbEntity
import com.example.mainapp.model.elements.room.ElementsDao
import com.example.mainapp.model.elements.room.entities.ElementDbEntity

@Database(
    version = 1,
    entities = [
        AccountDbEntity::class,
        ElementDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAccountsDao(): AccountsDao

    abstract fun getElementDao(): ElementsDao

}