package com.example.mainapp.model.elements.room

import androidx.room.*
import com.example.mainapp.model.elements.room.entities.ElementAdapterTuple
import com.example.mainapp.model.elements.room.entities.ElementDbEntity
import com.example.mainapp.model.elements.room.entities.ElementUpdateTextTuple
import kotlinx.coroutines.flow.Flow


@Dao
interface ElementsDao {

    @Query("SELECT * FROM elements WHERE userId = :userId")
    fun getByAccountId(userId: Long): Flow<List<ElementDbEntity?>>

    @Insert(entity = ElementDbEntity::class)
    fun createElement(elementDbEntity: ElementDbEntity)

    @Update(entity = ElementDbEntity::class)
    suspend fun updateText(element: ElementUpdateTextTuple)

    @Query("DELETE FROM elements WHERE id = :id")
    suspend fun deleteElementById(id: Long)
}