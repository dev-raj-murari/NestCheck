package com.nestcheck.app.core

import java.security.MessageDigest

/**
 * Salted SHA-256 for kid PINs so we never keep a raw PIN in a model object.
 * NOTE: a 4-digit PIN is brute-forceable offline no matter how it is hashed. This only
 * stops casual leaks; real protection = server-side verification + attempt lockout.
 */
object PinHasher {
    fun hash(salt: String, pin: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$salt:$pin".toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun matches(salt: String, pin: String, expectedHash: String): Boolean =
        expectedHash.isNotEmpty() &&
            MessageDigest.isEqual(hash(salt, pin).toByteArray(), expectedHash.toByteArray())
}
