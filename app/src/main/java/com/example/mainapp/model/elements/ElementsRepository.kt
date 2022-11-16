package com.example.mainapp.model.elements

import com.example.mainapp.model.elements.entities.Element
import kotlinx.coroutines.flow.Flow

interface ElementsRepository {


    suspend fun getElementsByAccountId(): Flow<List<Element>>

    suspend fun createElement(text: String)

    suspend fun updateElementText(id: Long, newText: String)

    suspend fun deleteElement(id: Long)

}