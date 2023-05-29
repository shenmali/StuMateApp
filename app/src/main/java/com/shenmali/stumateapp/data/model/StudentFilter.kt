package com.shenmali.stumateapp.data.model

data class StudentFilter(
    val type: Student.StudentType,
    val distanceToUniversity:  ClosedFloatingPointRange<Float> = 0f..10f,
    val availableTime: IntRange = 1..24,
)
