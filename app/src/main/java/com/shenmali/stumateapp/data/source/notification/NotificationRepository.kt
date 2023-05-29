package com.shenmali.stumateapp.data.source.notification

import com.shenmali.stumateapp.data.model.UniqueId

interface NotificationRepository {
    suspend fun notifyMatchRequestSent(targetStudentFcmToken: String)
    suspend fun notifyMatchRequestAccepted(targetStudentFcmToken: String)
}