package com.shenmali.stumateapp.data.model.validation

import com.shenmali.stumateapp.data.model.Student
import com.shenmali.stumateapp.data.model.credentials.EditProfileData
import javax.inject.Inject

class EditProfileValidator @Inject constructor() : Validator<EditProfileData> {

    override fun validate(args: EditProfileData) {
        if (args.firstName.length < 2) {
            error("Name must be at least 2 characters")
        }
        if (args.lastName.length < 2) {
            error("Surname must be at least 2 characters")
        }
        if (args.department.length < 2) {
            error("Department must be two character at least")
        }
        if (args.grade.isBlank()) {
            error("Grade can not be null")
        }
        if (args.grade.toIntOrNull() == null) {
            error("Grade must be number")
        }
        if (args.grade.toInt() !in 1..6) {
            error("Grade must be between 1-6")
        }
        when(args.state) {
            Student.StudentType.PROVIDER, Student.StudentType.SEEKER -> {
                if (args.distanceToUniversity.isBlank()) {
                    error("Distance to university cannot be left blank")
                }
                if (args.distanceToUniversity.toFloatOrNull() == null) {
                    error("Distance to university must be a number")
                }
                if (args.distanceToUniversity.toFloat() < 0) {
                    error("Distance to university must be greater than 0")
                }
                if(args.state == Student.StudentType.PROVIDER && args.homeAddress == null) {
                    error("Home location cannot be left blank")
                }
            }
            Student.StudentType.IDLE -> Unit
        }
        // phone is must be blank or in format +90 5XX XXX XXXX
        val phoneRegex = "^\\+90 5[0-9]{2} [0-9]{3} [0-9]{4}$".toRegex()
        if (args.phone.isNotBlank() && !args.phone.matches(phoneRegex)) {
            error("Invalid Numver")
        }
    }

}