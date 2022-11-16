package com.example.mainapp.screens.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainapp.model.EmptyFieldException
import com.example.mainapp.model.Field
import com.example.mainapp.model.elements.ElementsRepository
import com.example.mainapp.model.elements.entities.Element
import com.example.mainapp.utils.MutableUnitLiveEvent
import com.example.mainapp.utils.publishEvent
import com.example.mainapp.utils.requireValue
import com.example.mainapp.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditElementViewModel(
    private val elementsRepository: ElementsRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()


    fun editElementSaveButtonPressed(id: Long, text: String) {
        viewModelScope.launch {
            showProgress()
            try {
                if (id != -1L) {
                    elementsRepository.updateElementText(id, text)
                } else {
                    elementsRepository.createElement(text)
                }
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyTextError = e.field == Field.Text,
            editInInProgress = false
        )
    }

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _state.value = State(editInInProgress = true)
    }

    data class State(
        val emptyTextError: Boolean = false,
        val editInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = editInInProgress
        val enableViews: Boolean get() = !editInInProgress
    }
}