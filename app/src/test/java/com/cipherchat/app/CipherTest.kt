package com.cipherchat.app

import com.cipherchat.app.crypto.caesarDecrypt
import com.cipherchat.app.crypto.caesarEncrypt
import com.cipherchat.app.crypto.createEncryptedPackage
import com.cipherchat.app.crypto.decryptMessage
import com.cipherchat.app.crypto.encryptMessage
import com.cipherchat.app.crypto.generateRandomKey
import com.cipherchat.app.crypto.normalizeVigenereKey
import com.cipherchat.app.crypto.parseEncryptedPackage
import com.cipherchat.app.crypto.vigenereDecrypt
import com.cipherchat.app.crypto.vigenereEncrypt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CipherTest {
    @Test
    fun caesarHelloShift3() {
        assertEquals("KHOOR", caesarEncrypt("HELLO", 3))
        assertEquals("HELLO", caesarDecrypt("KHOOR", 3))
    }

    @Test
    fun caesarPreservesPunctuationAndNumbers() {
        assertEquals("Khoor, Zruog! 123", caesarEncrypt("Hello, World! 123", 3))
    }

    @Test
    fun vigenereClassic() {
        assertEquals("RIJVS", vigenereEncrypt("HELLO", "KEY"))
        assertEquals("HELLO", vigenereDecrypt("RIJVS", "KEY"))
        assertEquals("PYWSQ", vigenereEncrypt("KHOOR", "FRIEZA"))
    }

    @Test
    fun keyNormalization() {
        assertEquals("MYSECRETKEY", normalizeVigenereKey("My Secret Key!"))
    }

    @Test
    fun outputHasNoPrefix() {
        val encrypted = encryptMessage("Hello there", 3, "FRIEZA")
        assertEquals(encrypted.vigenereOutput, encrypted.packaged)
        assertFalse(encrypted.packaged.startsWith("CC1|"))
        assertFalse(encrypted.packaged.contains("FRIEZA"))
    }

    @Test
    fun legacyFormattedMessagesStillDecrypt() {
        val encrypted = encryptMessage("Old style message", 7, "KEY")
        val legacy = createEncryptedPackage(encrypted.packaged, 7)
        val parsed = parseEncryptedPackage(legacy)
        assertTrue(parsed.isFormatted)
        assertEquals(7, parsed.shift)
        val decrypted = decryptMessage(legacy, "KEY", 1)
        assertEquals("Old style message", decrypted.originalMessage)
        assertEquals(7, decrypted.shift)
    }

    @Test
    fun englishSentenceRoundTrip() {
        val original = "I will meet you tomorrow at 8 PM."
        val encrypted = encryptMessage(original, 3, "FRIEZA")
        assertTrue(encrypted.verified)
        val decrypted = decryptMessage(encrypted.packaged, "FRIEZA", 3)
        assertEquals(original, decrypted.originalMessage)
        assertTrue(decrypted.structureVerified)
    }

    @Test
    fun randomKeyLengthAndCharset() {
        listOf(6, 8, 12, 16, 24).forEach { length ->
            val key = generateRandomKey(length)
            assertEquals(length, key.length)
            assertTrue(key.all { it in 'A'..'Z' })
        }
    }
}
