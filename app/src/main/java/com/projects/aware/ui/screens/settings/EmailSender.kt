package com.projects.aware.ui.screens.settings

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


fun sendEmailFeedback(name: String, email: String = "hema.ko.services@gmail.com", message: String, onComplete: (Boolean) -> Unit) {
    val url = "https://formspree.io/f/xeokkbvk" // replace with your actual endpoint

    val json = """
        {
          "name": "$name",
          "email": "$email",
          "message": "$message"
        }
    """.trimIndent()

    val client = OkHttpClient()
    val requestBody = json.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .header("Accept", "application/json")
        .build()

    Thread {
        try {
            client.newCall(request).execute().use { response ->
                onComplete(response.isSuccessful)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false)
        }
    }.start()
}
