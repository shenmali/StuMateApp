package com.shenmali.stumateapp.data.source.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.shenmali.stumateapp.data.model.UniqueId
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
) : AuthRepository {
    override fun getCurrentStudentId(): UniqueId? {
        return auth.currentUser?.uid?.let { UniqueId(it) }
    }

    override suspend fun login(email: String, password: String): UniqueId {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                error("Could not login")
            }
            return UniqueId(result.user!!.uid)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException,
                -> {
                    error("Email or password is incorrect")
                }

                else -> {
                    error("An error occurred while logging in")
                }
            }
        }
    }

    override suspend fun signup(email: String, password: String): UniqueId {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                error("Registration failed")
            }
            return UniqueId(result.user!!.uid)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException,
                -> {
                    error("Email or password is incorrect")
                }

                is FirebaseAuthUserCollisionException -> {
                    error("This email address is already in use")
                }

                else -> {
                    error("An error occurred while registering")
                }
            }
        }
    }

    override suspend fun sendResetPasswordEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    error("No registered user found with this email address")
                }

                else -> {
                    error("Error When Reset Password")
                }
            }
        }

    }

    override fun logout() {
        auth.signOut()
    }
}