package com.main.builder.api

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
        "{\"model\":\"mistral-large-latest\",\"response_format\":{\"type\":\"json_object\"},\"messages\":[{\"role\":\"user\",\"content\":\"Try\"}],\"temperature\":0.7,\"top_p\":1,\"max_tokens\":1080,\"stream\":false,\"safe_prompt\":false,\"random_seed\":1337}"
    )

    constructor() : this(
        URL("https://api.mistral.ai/v1/chat/completions"),
        "{\"model\":\"mistral-large-latest\",\"response_format\":{\"type\":\"json_object\"},\"messages\":[{\"role\":\"user\",\"content\":\"Try\"}],\"temperature\":0.7,\"top_p\":1,\"max_tokens\":1080,\"stream\":false,\"safe_prompt\":false,\"random_seed\":1337}"
    )

    fun setUserRequest(userRequest: String): String {
        this.userRequest = this.userRequest.replace("Try", userRequest);
        return this.userRequest;
    }

    fun renewUserRequest() {
        this.userRequest = "{\"model\":\"mistral-large-latest\",\"response_format\":{\"type\":\"json_object\"},\"messages\":[{\"role\":\"user\",\"content\":\"Try\"}],\"temperature\":0.7,\"top_p\":1,\"max_tokens\":1080,\"stream\":false,\"safe_prompt\":false,\"random_seed\":1337}"
    }

    fun getCallString(): String {
        return userRequest;
    }

    fun sendStandardCall(): String {
        return try {
            val client = OkHttpClient();
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("model", "mistral-small-latest")
                .addHeader("Authorization", "Bearer mD3JJeHqBGWmJF5YhCB2qNba7qwDEcxa")
                .post(userRequest.toRequestBody(mediaType))
                .build();
            val res = client.newCall(request).execute();
            return res.body?.string().toString();
        } catch (e: Exception) {
            e.message.toString()
        }
    }


}
