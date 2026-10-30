package com.focusvault.app

import com.focusvault.app.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun hashSha256_computesKnownHashCorrectly() {
        // Known salted SHA-256 hash for "password123" with salt "FocusVault_Crypt_Salt_Offline_v1"
        // echo -n "FocusVault_Crypt_Salt_Offline_v1:password123" | shasum -a 256
        val input = "password123"
        val expectedHash = "1928dcc2b8f7c93811de4fa9d1b6001209f15c98519614bb739c443a36b5c1b3"
        val actualHash = SecurityUtils.hashSha256(input)
        assertEquals(expectedHash, actualHash)
    }

    @Test
    fun verifyPasscode_verifiesLegacyUnsaltedHash() {
        val passcode = "password123"
        val legacyHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
        assertTrue(SecurityUtils.verifyPasscode(passcode, legacyHash))
    }

    @Test
    fun hashSha256_differentInputsProduceDifferentHashes() {
        val hash1 = SecurityUtils.hashSha256("1234")
        val hash2 = SecurityUtils.hashSha256("1235")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verifyPasscode_matchesCorrectPasscode() {
        val passcode = "secretFocusPIN99"
        val hash = SecurityUtils.hashSha256(passcode)

        assertTrue(SecurityUtils.verifyPasscode(passcode, hash))
        assertFalse(SecurityUtils.verifyPasscode("wrongPIN", hash))
        assertFalse(SecurityUtils.verifyPasscode("", hash))
        assertFalse(SecurityUtils.verifyPasscode(passcode, null))
    }
}

