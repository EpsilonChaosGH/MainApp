package com.example.mainapp.model.accounts.room

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.mainapp.model.AccountAlreadyExistsException
import com.example.mainapp.model.AuthException
import com.example.mainapp.model.EmptyFieldException
import com.example.mainapp.model.Field
import com.example.mainapp.model.accounts.AccountsRepository
import com.example.mainapp.model.accounts.entities.SignUpData
import com.example.mainapp.model.settings.AppSettings
import com.example.mainapp.utils.AsyncLoader
import com.example.mainapp.model.accounts.entities.Account
import com.example.mainapp.model.accounts.room.entities.AccountDbEntity
import com.example.mainapp.model.accounts.room.entities.AccountUpdateUsernameTuple
import com.example.mainapp.model.room.wrapSQLiteException
import com.example.mainapp.utils.security.SecurityUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class AccountsRepositoryImpl(
    private val accountsDao: AccountsDao,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher,
    private val securityUtils: SecurityUtils
) : AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    override suspend fun isSignedIn(): Boolean {
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: CharArray) =
        wrapSQLiteException(ioDispatcher) {
            if (email.isBlank()) throw EmptyFieldException(Field.Email)
            if (password.isEmpty()) throw EmptyFieldException(Field.Password)

            val accountId = findAccountIdByEmailAndPassword(email = email, password = password)
            appSettings.setCurrentAccountId(accountId)
            currentAccountIdFlow.get().value = AccountId(accountId)

            return@wrapSQLiteException
        }

    override suspend fun signUp(signUpData: SignUpData) = wrapSQLiteException(ioDispatcher) {
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


    override suspend fun updateAccountUsername(newUsername: String) =
        wrapSQLiteException(ioDispatcher) {
            if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)

            val accountId = appSettings.getCurrentAccountId()
            if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

            updateUsernameForAccountId(accountId, newUsername)

            currentAccountIdFlow.get().value = AccountId(accountId)
            return@wrapSQLiteException
        }

    private suspend fun createAccount(signUpData: SignUpData) {
        try {
            val entity = AccountDbEntity.fromSignUpData(signUpData, securityUtils)
            accountsDao.createAccount(entity)
        } catch (e: SQLiteConstraintException) {
            val appException = AccountAlreadyExistsException()
            appException.initCause(e)
            throw appException
        }
    }

    private fun getAccountById(accountId: Long): Flow<Account?> {
        return accountsDao.getById(accountId).map { it?.toAccount() }
    }

    private suspend fun updateUsernameForAccountId(accountId: Long, newUsername: String) {
        accountsDao.updateUsername(AccountUpdateUsernameTuple(accountId, newUsername))
    }

    private suspend fun findAccountIdByEmailAndPassword(email: String, password: CharArray): Long {
        val tuple = accountsDao.findByEmail(email) ?: throw AuthException()

        val saltBytes = securityUtils.stringToBytes(tuple.salt)
        val hashBytes = securityUtils.passwordToHash(password, saltBytes)
        val hashString = securityUtils.bytesToString(hashBytes)
        password.fill('*')

        Log.e("aaa tuple",tuple.hash.length.toString())
        Log.e("aaa tuple",tuple.hash)

        Log.e("aaa hashString",hashString.length.toString())
        Log.e("aaa hashString",hashString)

        println(hashString)
        println(tuple.hash)

        if (tuple.hash != hashString) throw AuthException()

        Log.e("aaa 1", "!!!!!!!!!!!!!!!!! ")
        return tuple.id
    }

    private class AccountId(val value: Long)
}