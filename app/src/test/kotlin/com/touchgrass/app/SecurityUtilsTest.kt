package com.touchgrass.app

import com.touchgrass.app.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun hashSha256_computesKnownHashCorrectly() {
        // Known salted SHA-256 hash for "password123" with salt "TouchGrass_Crypt_Salt_Offline_v1"
        // echo -n "TouchGrass_Crypt_Salt_Offline_v1:password123" | shasum -a 256
        val input = "password123"
        val expectedHash = "726c173e7984bde1d8f417e412c837ac11efbb084ed500227b678e9db95ca97d"
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

