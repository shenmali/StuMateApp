package com.shenmali.stumateapp.data.source.localstorage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.shenmali.stumateapp.data.model.Student
import com.shenmali.stumateapp.data.model.toJson

class LocalStorageRepositoryImpl(
    private val prefs: SharedPreferences,
) : LocalStorageRepository {
    override fun saveStudent(student: Student) {
        prefs.edit {
            // save student as json string
            putString(Keys.STUDENT_KEY, student.toJson())
        }
    }

    override fun getStudent(): Student? {
        val studentJson = prefs.getString(Keys.STUDENT_KEY, null) ?: return null
        return Student.fromJson(studentJson)
    }

    override fun clearStudent() {
        prefs.edit {
            remove(Keys.STUDENT_KEY)
        }
    }

    private object Keys {
        const val STUDENT_KEY = "student"
    }
}