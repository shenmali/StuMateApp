package com.shenmali.stumateapp.data.source.localstorage

import com.shenmali.stumateapp.data.model.Student

interface LocalStorageRepository {
    fun saveStudent(student: Student)
    fun getStudent(): Student?
    fun clearStudent()
}