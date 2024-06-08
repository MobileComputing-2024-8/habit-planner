package com.example.habit_planner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OPENAIService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getChatCompletion(@Body request: ChatRequest): Call<ChatResponse>
}