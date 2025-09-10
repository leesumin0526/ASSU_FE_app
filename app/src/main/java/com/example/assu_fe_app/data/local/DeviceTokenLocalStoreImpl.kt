package com.example.assu_fe_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.deviceTokenDataStore by preferencesDataStore(name = "device_token")
private object Keys { val TOKEN_ID = longPreferencesKey("token_id") }

@Singleton
class DeviceTokenLocalStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceTokenLocalStore {

    override suspend fun saveTokenId(tokenId: Long) {
        context.deviceTokenDataStore.edit { it[Keys.TOKEN_ID] = tokenId }
    }
    override suspend fun getTokenId(): Long? =
        context.deviceTokenDataStore.data.map { it[Keys.TOKEN_ID] }.first()

    override suspend fun clearTokenId() {
        context.deviceTokenDataStore.edit { it.remove(Keys.TOKEN_ID) }
    }
}
