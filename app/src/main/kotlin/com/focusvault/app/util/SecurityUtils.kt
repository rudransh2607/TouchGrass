package com.focusvault.app.util

import java.security.MessageDigest

/**
 * Utility functions for cryptographic hashing and verification.
 * FocusVault securely hashes all user passcodes using salted SHA-256 before storing.
 */
object SecurityUtils {

    private const val APP_SALT = "FocusVault_Crypt_Salt_Offline_v1"

    /**
     * Hashes the input string using salted SHA-256 and returns a lowercase 64-character hexadecimal string.
     */
    fun hashSha256(input: String): String {
        val saltedInput = "$APP_SALT:$input"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun hashSha256Legacy(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Safely verifies if the candidate input matches the expected SHA-256 hex string
     * using constant-time byte comparison to protect against timing attacks.
     */
    fun verifyPasscode(candidatePasscode: String, expectedHashHex: String?): Boolean {
        if (expectedHashHex.isNullOrBlank() || candidatePasscode.isBlank()) {
            return false
        }
        val candidateSalted = hashSha256(candidatePasscode)
        if (MessageDigest.isEqual(
                candidateSalted.toByteArray(Charsets.UTF_8),
                expectedHashHex.toByteArray(Charsets.UTF_8)
            )
        ) {
            return true
        }

        // Backward compatibility fallback for pre-existing unsalted hashes
        val candidateLegacy = hashSha256Legacy(candidatePasscode)
        return MessageDigest.isEqual(
            candidateLegacy.toByteArray(Charsets.UTF_8),
            expectedHashHex.toByteArray(Charsets.UTF_8)
        )
    }
}
