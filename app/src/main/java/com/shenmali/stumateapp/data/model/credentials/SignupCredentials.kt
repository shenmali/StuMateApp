package com.shenmali.stumateapp.data.model.credentials

import android.net.Uri

data class SignupCredentials(
    val imageUri: Uri?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)
