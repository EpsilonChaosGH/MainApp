package com.example.mainapp.model.accounts

import com.example.mainapp.model.accounts.entities.Account
import com.example.mainapp.model.accounts.entities.SignUpData
import kotlinx.coroutines.flow.Flow


interface AccountsRepository {

    suspend fun isSignedIn(): Boolean

    suspend fun signIn(email: String, password: CharArray)

    suspend fun signUp(signUpData: SignUpData)

    suspend fun logout()

    suspend fun getAccount(): Flow<Account?>

    suspend fun updateAccountUsername(newUsername: String)

}