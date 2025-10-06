package com.ssu.assu.data.local

import javax.inject.Inject

class AccessTokenProviderImpl @Inject constructor(
    private val authTokenLocalStore: AuthTokenLocalStore
) : AccessTokenProvider {
    override fun accessToken(): String? {
        return authTokenLocalStore.getAccessToken()
    }
}