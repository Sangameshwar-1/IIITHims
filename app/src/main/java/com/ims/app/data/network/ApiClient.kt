package com.ims.app.data.network

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://iiit-hims.vercel.app/api/"
    
    private var retrofit: Retrofit? = null
    private var authApiInstance: AuthApi? = null
    private var studentApiInstance: StudentApi? = null
    private var examApiInstance: ExamApi? = null
    private var dashboardApiInstance: DashboardApi? = null
    private var courseApiInstance: CourseApi? = null

    fun initialize(context: Context) {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            
        authApiInstance = retrofit!!.create(AuthApi::class.java)
        studentApiInstance = retrofit!!.create(StudentApi::class.java)
        examApiInstance = retrofit!!.create(ExamApi::class.java)
        dashboardApiInstance = retrofit!!.create(DashboardApi::class.java)
        courseApiInstance = retrofit!!.create(CourseApi::class.java)
    }

    val authApi: AuthApi
        get() = authApiInstance ?: throw IllegalStateException("ApiClient not initialized")
    val studentApi: StudentApi
        get() = studentApiInstance ?: throw IllegalStateException("ApiClient not initialized")
    val examApi: ExamApi
        get() = examApiInstance ?: throw IllegalStateException("ApiClient not initialized")
    val dashboardApi: DashboardApi
        get() = dashboardApiInstance ?: throw IllegalStateException("ApiClient not initialized")
    val courseApi: CourseApi
        get() = courseApiInstance ?: throw IllegalStateException("ApiClient not initialized")
}
