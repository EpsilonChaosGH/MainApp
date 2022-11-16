package com.example.mainapp.model.elements.room

import android.database.sqlite.SQLiteConstraintException
import com.example.mainapp.model.AccountAlreadyExistsException
import com.example.mainapp.model.EmptyFieldException
import com.example.mainapp.model.Field
import com.example.mainapp.model.elements.ElementsRepository
import com.example.mainapp.model.elements.entities.Element
import com.example.mainapp.model.elements.room.entities.ElementDbEntity
import com.example.mainapp.model.elements.room.entities.ElementUpdateTextTuple
import com.example.mainapp.model.room.wrapSQLiteException
import com.example.mainapp.model.settings.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class RoomElementRepository(
    private val elementsDao: ElementsDao,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
) : ElementsRepository {


    override suspend fun getElementsByAccountId(): Flow<List<Element>> {
        delay(1100)
        val currentAccountId = appSettings.getCurrentAccountId()
        return elementsDao.getByAccountId(currentAccountId).map { it.map { it!!.toElement() } }
    }

    override suspend fun createElement(text: String) = wrapSQLiteException(ioDispatcher) {
        if (text.isBlank()) throw EmptyFieldException(Field.Text)
        try {
            delay(1100)
            val entity = ElementDbEntity(
                id = 0,
                userId = appSettings.getCurrentAccountId(),
                text = text,
                System.currentTimeMillis(),
                false
            )
            elementsDao.createElement(entity)
        } catch (e: SQLiteConstraintException) {
            val appException = AccountAlreadyExistsException()
            appException.initCause(e)
            throw appException
        }
        return@wrapSQLiteException
    }

    override suspend fun updateElementText(id: Long, newText: String) =
        wrapSQLiteException(ioDispatcher) {
            if (newText.isBlank()) throw EmptyFieldException(Field.Text)
            delay(1100)
            elementsDao.updateText(ElementUpdateTextTuple(id, newText))
        }

    override suspend fun deleteElement(id: Long) = wrapSQLiteException(ioDispatcher) {
        delay(1100)
        elementsDao.deleteElementById(id)
    }
}