package com.example.mainapp.screens.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainapp.model.EmptyFieldException
import com.example.mainapp.model.Field
import com.example.mainapp.model.elements.ElementsRepository
import com.example.mainapp.model.elements.entities.Element
import com.example.mainapp.model.elements.entities.ElementListItem
import com.example.mainapp.utils.MutableUnitLiveEvent
import com.example.mainapp.utils.requireValue
import com.example.mainapp.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val elementsRepository: ElementsRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private var _elementState = MutableLiveData<List<ElementListItem>>()
    val elementState = _elementState.share()

    private val elementIdsInProgress = mutableSetOf<Long>()


    private fun addProgressTo(id: Long) {
        elementIdsInProgress.add(id)
        notifyUpdates()
    }

    private fun removeProgressFrom(id: Long) {
        elementIdsInProgress.remove(id)
        notifyUpdates()
    }

    private fun isInProgress(element: Element): Boolean {
        return elementIdsInProgress.contains(element.id)
    }

    private fun notifyUpdates() {
        _elementState.value = _elementState.value?.map { element ->
            ElementListItem(
                element.element,
                isInProgress(element.element)
            )
        }
    }

    fun getElements() {
        viewModelScope.launch {
            if (_elementState.value == null) {
                showProgress()
                elementsRepository.getElementsByAccountId().collect() {
                    _elementState.value =
                        it.map { element -> ElementListItem(element, isInProgress(element)) }
                    endProgress()
                }
            }
        }
    }

    fun deleteElement(id: Long) {
        viewModelScope.launch {
            addProgressTo(id)
            elementsRepository.deleteElement(id)
            removeProgressFrom(id)
        }
    }

    private fun endProgress() {
        _state.value = _state.requireValue().copy(
            dashboardInInProgress = false
        )
    }

    private fun showProgress() {
        _state.value = State(dashboardInInProgress = true)
    }

    data class State(
        val dashboardInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = dashboardInInProgress
        val enableViews: Boolean get() = !dashboardInInProgress
    }
}