package com.shenmali.stumateapp.data.model.credentials

import com.shenmali.stumateapp.data.model.Student

data class EditProfileData(
    val firstName: String,
    val lastName: String,
    val department: String,
    val grade: String,
    val state: Student.StudentType,
    val distanceToUniversity: String,
    val availableTime: String,
    val homeAddress: Student.HomeAddress?,
    val email: String,
    val phone: String,
)
