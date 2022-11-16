package com.example.mainapp.model.elements.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mainapp.model.elements.entities.Element


@Entity(
    tableName = "elements"
)
data class ElementDbEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "userId") val userId: Long,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "done") val done: Boolean
) {

    fun toElement(): Element = Element(
        id = id,
        userId = userId,
        text = text,
        timestamp = timestamp,
        done = done
    )
}