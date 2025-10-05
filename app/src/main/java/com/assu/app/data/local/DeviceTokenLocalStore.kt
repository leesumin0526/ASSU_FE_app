package com.assu.app.data.local

interface DeviceTokenLocalStore {
    suspend fun saveDeviceTokenId(tokenId: Long)
    suspend fun getDeviceTokenId(): Long?
    suspend fun clearDeviceTokenId()
}