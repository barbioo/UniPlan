package com.main.builder.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class RequestSender(
    private val url: URL,
    private var userRequest: String
) {

    companion object {
        private fun buildRequest(): String {
            val requestBody = buildJsonObject {
                put("model", "open-mistral-7b")
                put("response_format", buildJsonObject {
                    put("type", "json_object")
                })
                put("messages", buildJsonArray {
                    add(buildJsonObject {
                        put("role", "user")
                        put("content", "Try")
                    })
                })
                put("temperature", 0.7)
                put("top_p", 1)
                put("max_tokens", 1080)
                put("stream", false)
                put("safe_prompt", false)
                put("random_seed", 1337)
            }
            return Json.encodeToString(JsonObject.serializer(), requestBody)
        }
    }
    constructor(url: String) : this(
        URL(url),
        buildRequest()
        )

    constructor() : this(
        URL("https://api.mistral.ai/v1/chat/completions"),
        buildRequest()
    )

    fun setUserRequest(userRequest: String): String {
        this.userRequest = this.userRequest.replace("Try", userRequest);
        return this.userRequest;
    }

    fun renewUserRequest() {
        this.userRequest = buildRequest()
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
                .addHeader("model", "open-mistral-7b")
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
