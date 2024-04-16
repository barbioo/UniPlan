package com.builder.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class RequestSender(
    private val url: URL,
    private var userRequest: String
) {
    constructor(url: String) : this(
        URL(url),
        "{\n  \"model\": \"open-mistral-7b\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"Try\"\n    }\n  ],\n  \"temperature\": 0.7,\n  \"top_p\": 1,\n  \"max_tokens\": 10,\n  \"stream\": false,\n  \"safe_prompt\": false,\n  \"random_seed\": 1337\n}"
    )

    constructor() : this(
        URL("https://api.mistral.ai/v1/chat/completions"),
        "{\n  \"model\": \"open-mistral-7b\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"Try\"\n    }\n  ],\n  \"temperature\": 0.7,\n  \"top_p\": 1,\n  \"max_tokens\": 10,\n  \"stream\": false,\n  \"safe_prompt\": false,\n  \"random_seed\": 1337\n}"
    )

    fun setUserRequest(userRequest: String): String {
        this.userRequest = this.userRequest.replace("Try", userRequest);
        return this.userRequest;
    }

    fun renewUserRequest() {
        this.userRequest = "{\n  \"model\": \"open-mistral-7b\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"Try\"\n    }\n  ],\n  \"temperature\": 0.7,\n  \"top_p\": 1,\n  \"max_tokens\": 10,\n  \"stream\": false,\n  \"safe_prompt\": false,\n  \"random_seed\": 1337\n}"
    }

    //restituisce una risposta json
    fun sendStandardCall(): String {
        return try {
            val client = OkHttpClient();
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("model", "mistral-small-latest")
                .addHeader("Authorization", "Bearer LjKG1bXM7ILjEK2iwdjyhFU4b6dOAjNy")
                .post(userRequest.toRequestBody(mediaType))
                .build();
            val res = client.newCall(request).execute();
            if (res.isSuccessful) {
                return res.body?.string().toString();
            } else {
                return throw Exception("Request not ok");
            }
        } catch (e: Exception) {
            e.message.toString()
        }
    }
    //questa risposta è scritta nel file "x" che restituisce il content con data e nome materia

}
