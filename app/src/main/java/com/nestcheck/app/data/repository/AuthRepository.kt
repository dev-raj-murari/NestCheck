package com.nestcheck.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nestcheck.app.BuildConfig
import com.nestcheck.app.data.model.ParentUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, pass: String): Result<Boolean>
    suspend fun register(email: String, pass: String, name: String): Result<Boolean>
    suspend fun logout()
    suspend fun getUserProfile(uid: String): Result<ParentUser>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * A wrong password / unknown user / duplicate email is ALWAYS a failure.
     * The offline "demo" fallback only exists in debug builds, and only when Firebase itself is
     * unreachable or misconfigured (dummy google-services.json) - never for bad credentials.
     */
    private fun mayFallBackToDemo(e: Exception): Boolean =
        BuildConfig.DEBUG &&
            e !is FirebaseAuthInvalidCredentialsException &&
            e !is FirebaseAuthInvalidUserException &&
            e !is FirebaseAuthUserCollisionException

    override suspend fun login(email: String, pass: String): Result<Boolean> = runCatching {
        val cleanEmail = email.trim()
        try {
            val result = auth.signInWithEmailAndPassword(cleanEmail, pass).await()
            profileRepository.updateParentProfile(
                name = result.user?.displayName ?: cleanEmail.substringBefore("@"),
                email = cleanEmail
            )
            true
        } catch (e: Exception) {
            if (!mayFallBackToDemo(e)) throw e
            profileRepository.updateParentProfile(
                name = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = cleanEmail
            )
            true
        }
    }

    override suspend fun register(email: String, pass: String, name: String): Result<Boolean> = runCatching {
        val cleanEmail = email.trim()
        try {
            val result = auth.createUserWithEmailAndPassword(cleanEmail, pass).await()
            val user = result.user
            if (user != null) {
                firestore.collection("users")
                    .document(user.uid)
                    .set(ParentUser(uid = user.uid, email = cleanEmail, name = name))
                    .await()
            }
        } catch (e: Exception) {
            if (!mayFallBackToDemo(e)) throw e
        }
        profileRepository.updateParentProfile(name = name, email = cleanEmail)
        true
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
    }

    override suspend fun getUserProfile(uid: String): Result<ParentUser> = runCatching {
        try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.toObject(ParentUser::class.java) ?: profileRepository.parentProfile.value
        } catch (e: Exception) {
            profileRepository.parentProfile.value
        }
    }
}
