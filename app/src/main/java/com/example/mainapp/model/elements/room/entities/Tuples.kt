package com.example.mainapp.model.elements.room.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey


data class ElementAdapterTuple(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "done") val done: Boolean
)

data class ElementUpdateTextTuple(
    @ColumnInfo(name = "id") @PrimaryKey val id: Long,
    @ColumnInfo(name = "text") val text: String
)
