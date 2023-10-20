package org.softwaremaestro.domain.tutoring_get.entity

data class TutoringVO(
    val description: String?,
    val schoolSubject: String?,
    val recordFileUrl: String?,
    val tutoringId: String?,
    val questionId: String?,
    val schoolLevel: String?,
    val tutoringDate: String?,
    val opponentName: String?,
    val opponentProfileImage: String?,
    val questionImage: String?
)