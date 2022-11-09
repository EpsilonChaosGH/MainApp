package com.example.mainapp.model.accounts.entities


data class Account(
    val id: Long,
    val username: String,
    val email: String,
    var password: String = "123",
    val createdAt: Long = UNKNOWN_CREATED_AT
) {
    companion object {
        const val UNKNOWN_CREATED_AT = 0L
    }
}