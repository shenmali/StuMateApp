package com.shenmali.stumateapp.data.source.notification

import com.shenmali.stumateapp.BuildConfig
import com.shenmali.stumateapp.data.source.localstorage.LocalStorageRepository
import com.shenmali.stumateapp.data.source.notification.NotificationRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NotificationRepositoryImpl(
    private val client: HttpClient,
    private val localStorageRepository: LocalStorageRepository,
) : NotificationRepository {

    @OptIn(InternalAPI::class)
    private suspend fun sendNotification(
        fcmToken: String,
        notificationTitle: String,
        notificationBody: String,
    ) {
        val response = client.post {
            url("https://fcm.googleapis.com/fcm/send")
            // get server key from local.properties
            val serverKey = BuildConfig.FCM_SERVER_KEY
            header("Authorization", "key=${serverKey}")
            header("Content-Type", "application/json")
            body = buildJsonObject {
                put("to", fcmToken)
                put("notification", buildJsonObject {
                    put("title", notificationTitle)
                    put("body", notificationBody)
                })
            }.toString()
        }
        response.content
    }

    override suspend fun notifyMatchRequestSent(targetStudentFcmToken: String) {
        val currentStudent = localStorageRepository.getStudent()!!
        sendNotification(
            fcmToken = targetStudentFcmToken,
            notificationTitle = "Match Request",
            notificationBody = "${currentStudent.fullName} named student wants to match with you."
        )
    }

    override suspend fun notifyMatchRequestAccepted(targetStudentFcmToken: String) {
        val currentStudent = localStorageRepository.getStudent()!!
        sendNotification(
            fcmToken = targetStudentFcmToken,
            notificationTitle = "Your Match Request Has Been Accepted",
            notificationBody = "${currentStudent.fullName} named student has accepted your match request."
        )
    }
}