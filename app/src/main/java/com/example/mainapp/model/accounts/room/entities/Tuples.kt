package com.example.mainapp.model.accounts.room.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class AccountSignInTuple(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "salt") val salt: String,
)

data class AccountUpdateUsernameTuple(
    @ColumnInfo(name = "id") @PrimaryKey val id: Long,
    @ColumnInfo(name = "username") val username: String
)