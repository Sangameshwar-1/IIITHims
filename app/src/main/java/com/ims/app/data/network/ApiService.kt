package com.ims.app.data.network

import android.util.Log
import com.ims.app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Service to handle network requests, such as the required app identifier tracking.
 */
class ApiService {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
        
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun postAppIdentifier(identifier: String) {
        withContext(Dispatchers.IO) {
            try {
                val jsonPayload = """{"identifier":"$identifier"}"""
                val body = jsonPayload.toRequestBody(jsonMediaType)
                
                val request = Request.Builder()
                    .url(Constants.TRACKER_URL)
                    .post(body)
                    .build()
                    
                val response = client.newCall(request).execute()
                Log.d("ApiService", "Tracker POST response code: ${response.code}")
                response.close()
            } catch (e: Exception) {
                Log.e("ApiService", "Error posting tracker info: ${e.message}")
            }
        }
    }
}
