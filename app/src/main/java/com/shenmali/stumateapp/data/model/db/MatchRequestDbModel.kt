package com.shenmali.stumateapp.data.model.db

import com.google.firebase.firestore.DocumentId
import com.shenmali.stumateapp.data.model.MatchRequest
import com.shenmali.stumateapp.data.model.UniqueId

data class MatchRequestDbModel(
    @DocumentId val uid: String = "",
    val status: Status = Status.RECEIVED,
    val targetUser: UserDbModel = UserDbModel(),
) {
    enum class Status {
        SENT,
        RECEIVED,
        MATCHED,
    }
}

fun MatchRequestDbModel.toMatchRequest(): MatchRequest {
    return when (status) {
        MatchRequestDbModel.Status.SENT -> MatchRequest.Sent(
            uid = UniqueId(uid),
            targetStudent = targetUser.toStudent()
        )

        MatchRequestDbModel.Status.RECEIVED -> MatchRequest.Received(
            uid = UniqueId(uid),
            targetStudent = targetUser.toStudent()
        )

        MatchRequestDbModel.Status.MATCHED -> MatchRequest.Matched(
            uid = UniqueId(uid),
            targetStudent = targetUser.toStudent()
        )
    }
}