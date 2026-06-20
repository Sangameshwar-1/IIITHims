package com.ims.app.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        
        // Retrieve token from SharedPreferences
        val tokenManager = TokenManager.getInstance(context)
        val token = tokenManager.getToken()

        // If token exists, add it to the Authorization header
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
