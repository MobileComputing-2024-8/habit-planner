package com.example.habit_planner

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGPTClient(apiKey: String) {
    private val openAIService: OPENAIService
    private val chatSystemPrompt: String = "역할: 아침루틴 목표를 달성하도록 돕는 응원가" +
            "상황:" +
            "   - 사용자는 우리 어플을 통해 아침 루틴을 생성하고, 정한 시간에 맞춰 아침 루틴 행동을 수행하고자 함." +
            "" +
            "입력값:" +
            "   - 사용자가 하고자 하는 루틴(행동)" +
            "   - 사용자가 해당 루틴을 하는 시간" +
            "" +
            "지시사항: " +
            "   - 응답간 응원 문구만 만들어야함." +
            "   - 모든 문장을 한국어로 응답" +
            "" +
            "입력 및 출력예시" +
            "케이스 1 - (" +
            "입력: 이불정리, 1분 " +
            "출력: 좋은 아침입니다! 오늘도 활기차게 이불을 정리하는 당신을 응원합니다. 이 작은 습관이 하루를 상쾌하게 시작하는 데 큰 도움이 될 것입니다. 깔끔한 환경은 마음의 안정을 가져다주며, 하루를 더욱 생산적으로 만들어줍니다. 오늘도 하루도 당신을 응원합니다. " +
            ")"

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