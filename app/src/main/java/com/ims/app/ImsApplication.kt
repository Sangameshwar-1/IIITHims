package com.ims.app

import android.app.Application
import com.ims.app.data.repository.DashboardRepository
import com.ims.app.data.repository.ExamRepository
import com.ims.app.data.repository.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Application class for IMS. Initializes the repositories using manual dependency injection.
 */
class ImsApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val studentRepository: StudentRepository by lazy {
        StudentRepository()
    }

    val examRepository: ExamRepository by lazy {
        ExamRepository()
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository()
    }

    override fun onCreate() {
        super.onCreate()
        com.ims.app.data.network.ApiClient.initialize(this)
        sendAppIdentifier()
    }

    /**
     * Sends the APP_IDENTIFIER to the tracking endpoint on startup.
     * Non-blocking POST request as specified in the assignment.
     */
    private fun sendAppIdentifier() {
        try {
            val identifier = BuildConfig.APP_IDENTIFIER
            applicationScope.launch {
                try {
                    val client = OkHttpClient()
                    val json = """{"appIdentifier":"$identifier"}"""
                    val body = json.toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("https://project-tracker-0eju.onrender.com/api/data")
                        .post(body)
                        .build()
                    client.newCall(request).execute().close()
                } catch (_: Exception) {
                    // Silently ignore network errors
                }
            }
        } catch (_: Exception) {
            // BuildConfig field might not exist in some build variants
        }
    }
}
