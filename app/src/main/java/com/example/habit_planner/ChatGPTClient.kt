package com.example.habit_planner

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGPTClient(apiKey: String) {
    private val openAIService: OPENAIService
    private val chatSystemPrompt: String = "역할: 루틴 목표를 달성하도록 돕는 응원가" +
            "상황:" +
            "   - 사용자는 우리 어플을 통해 루틴을 생성하고, 시간에 맞춰 루틴 행동을 수행하고자 함." +
            "" +
            "입력값:" +
            "   - 사용자가 하고자 하는 루틴(행동)" +
            "   - 사용자가 해당 루틴을 동작하는데 걸리는 시간" +
            "" +
            "지시사항: " +
            "   - 응답간 응원 문구만 만들어야함." +
            "   - 모든 문장을 한국어로 응답하며 이모지, 이모티콘을 첨부하지 않는다." +
            "   - 1분당 최소 세 문장씩 응원 문구를 응답해야함" +
            "   - 관련된 명언이 있을 경우 명언을 추가할 것." +
            "   - 명언을 출력하는 경우 명언과 관련된 응원문구도 함께 추가할 것."

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        }.addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openAIService = retrofit.create(OPENAIService::class.java)
    }

    fun sendMessage(model: String, userPrompt: String, callback: Callback<ChatResponse>) {
        if (model == "gpt-3.5-turbo") {
            val messages = listOf(
                Message(role = "system", content = chatSystemPrompt),
                Message(role = "user", content = userPrompt)
            )
            val request = ChatRequest(model = "gpt-3.5-turbo-0125", messages = messages)
            getChatCompletion(request, callback)
        }
    }

    fun getChatCompletion(request: ChatRequest, callback: Callback<ChatResponse>) {
        val call = openAIService.getChatCompletion(request)
        call.enqueue(callback)
    }
}