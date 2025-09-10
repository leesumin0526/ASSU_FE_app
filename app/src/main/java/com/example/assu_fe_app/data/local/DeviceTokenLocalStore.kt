package com.example.assu_fe_app.data.local

interface DeviceTokenLocalStore {
    suspend fun saveTokenId(tokenId: Long)
    suspend fun getTokenId(): Long?
    suspend fun clearTokenId()
}