package com.assu.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceTokenLocalStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceTokenLocalStore {

    private val Context.deviceTokenDataStore by preferencesDataStore(name = "device_token")
    private object Keys { val TOKEN_ID = longPreferencesKey("token_id") }

    override suspend fun saveDeviceTokenId(tokenId: Long) {
        context.deviceTokenDataStore.edit { it[Keys.TOKEN_ID] = tokenId }
    }
    override suspend fun getDeviceTokenId(): Long? =
        context.deviceTokenDataStore.data.map { it[Keys.TOKEN_ID] }.first()

    override suspend fun clearDeviceTokenId() {
        android.util.Log.d("DeviceTokenLocalStore", "clearTokenId() called")
        context.deviceTokenDataStore.edit { it.remove(Keys.TOKEN_ID) }
        android.util.Log.d("DeviceTokenLocalStore", "clearTokenId() completed - tokenId removed")
    }
}
