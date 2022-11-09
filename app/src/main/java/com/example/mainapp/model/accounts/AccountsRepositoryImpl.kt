package com.example.mainapp.model.accounts

import com.example.mainapp.model.AccountAlreadyExistsException
import com.example.mainapp.model.AuthException
import com.example.mainapp.model.EmptyFieldException
import com.example.mainapp.model.Field
import com.example.mainapp.model.accounts.entities.SignUpData
import com.example.mainapp.model.settings.AppSettings
import com.example.mainapp.utils.AsyncLoader
import com.example.mainapp.model.accounts.entities.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class AccountsRepositoryImpl(
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    private val accounts = mutableListOf(
        Account(
            id = 0,
            username = "q",
            email = "q",
            password = "q"
        )
    )

    override suspend fun isSignedIn(): Boolean {
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: String) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        val accountId = findAccountIdByEmailAndPassword(email = email, password = password)
        appSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)
    }

    override suspend fun signUp(signUpData: SignUpData) {
        signUpData.validate()
        createAccount(signUpData)
    }

    override suspend fun logout() {
        appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .flatMapLatest { accountId ->
                if (accountId.value == AppSettings.NO_ACCOUNT_ID) {
                    flowOf(null)
                } else {
                    getAccountById(accountId.value)
                }
            }.flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)

        val currentAccount =
            accounts.firstOrNull { it.id == currentAccountIdFlow.get().value.value }
                ?: throw AuthException()
        val index = accounts.indexOfFirst { it.id == currentAccountIdFlow.get().value.value }
        accounts[index] = currentAccount.copy(username = newUsername)
        currentAccountIdFlow.get().value = AccountId(currentAccount.id)
    }

    private fun getAccountById(accountId: Long): Flow<Account?> {
        return flowOf(accounts.firstOrNull { it.id == accountId })
    }

    private fun createAccount(signUpData: SignUpData) {

        val account = accounts.firstOrNull { it.email == signUpData.email }
        if (account != null) throw AccountAlreadyExistsException()

        val newAccount = Account(
            id = Random(1000000).nextLong(),
            username = signUpData.username,
            email = signUpData.email,
            password = signUpData.password,
            createdAt = System.currentTimeMillis()
        )
        accounts.add(newAccount)
    }

    private fun findAccountIdByEmailAndPassword(email: String, password: String): Long {

        val account = accounts.firstOrNull { it.email == email }

        if (account != null && account.password == password) {
            return account.id
        } else {
            throw AuthException()
        }
    }

    private class AccountId(val value: Long)
}