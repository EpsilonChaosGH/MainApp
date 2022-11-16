package com.example.mainapp.model.elements.entities

data class Element(
    val id: Long,
    val userId: Long,
    val text: String,
    val timestamp: Long,
    val done: Boolean
)

data class ElementListItem(
    val element: Element,
    val isInProgress: Boolean
)