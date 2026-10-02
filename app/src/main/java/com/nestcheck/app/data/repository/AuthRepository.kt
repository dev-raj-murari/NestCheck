package com.nestcheck.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nestcheck.app.data.model.ParentUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, pass: String): Result<FirebaseUser>
    suspend fun register(email: String, pass: String, name: String): Result<FirebaseUser>
    suspend fun logout()
    suspend fun getUserProfile(uid: String): Result<ParentUser>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun login(email: String, pass: String): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, pass).await()
        result.user ?: throw Exception("User authentication failed")
    }

    override suspend fun register(email: String, pass: String, name: String): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val user = result.user ?: throw Exception("Registration failed")
        
        // Save Parent profile to Firestore
        val parentProfile = ParentUser(
            uid = user.uid,
            email = email,
            name = name
        )
        firestore.collection("users")
            .document(user.uid)
            .set(parentProfile)
            .await()
            
        user
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getUserProfile(uid: String): Result<ParentUser> = runCatching {
        val snapshot = firestore.collection("users").document(uid).get().await()
        snapshot.toObject(ParentUser::class.java) ?: ParentUser(uid = uid)
    }
}
