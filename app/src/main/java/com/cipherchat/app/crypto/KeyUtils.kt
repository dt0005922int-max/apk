package com.cipherchat.app.crypto

import java.security.SecureRandom

val RANDOM_KEY_LENGTHS = listOf(6, 8, 12, 16, 24)
const val DEFAULT_KEY_LENGTH = 12

data class KeyValidation(
    val valid: Boolean,
    val normalized: String,
    val error: String,
    val warning: String
)

fun normalizeVigenereKey(key: String): String {
    val normalized = buildString {
        for (ch in key) {
            if (ch in 'A'..'Z' || ch in 'a'..'z') {
                append(ch.uppercaseChar())
            }
        }
    }
    require(normalized.isNotEmpty()) {
        "Secret key must contain at least one alphabetic letter. Spaces and other characters are removed during normalization."
    }
    return normalized
}

fun getKeyValidation(key: String): KeyValidation {
    val strippedCount = key.length - key.count { it in 'A'..'Z' || it in 'a'..'z' }
    return try {
        val normalized = normalizeVigenereKey(key)
        KeyValidation(
            valid = true,
            normalized = normalized,
            error = "",
            warning = if (strippedCount > 0) {
                "Non-letter characters are removed internally. Normalized key: $normalized"
            } else {
                ""
            }
        )
    } catch (error: Exception) {
        KeyValidation(
            valid = false,
            normalized = "",
            error = if (key.isBlank()) {
                "Enter a shared secret key containing at least one letter."
            } else {
                error.message ?: "Secret key is invalid."
            },
            warning = ""
        )
    }
}

fun generateRandomKey(length: Int): String {
    require(length in RANDOM_KEY_LENGTHS) { "Random key length must be 6, 8, 12, 16, or 24." }
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val maxUnbiased = 26 * (256 / 26)
    val random = SecureRandom()
    val chars = StringBuilder(length)
    val buffer = ByteArray(length)
    while (chars.length < length) {
        random.nextBytes(buffer)
        for (value in buffer) {
            if (chars.length >= length) break
            val unsigned = value.toInt() and 0xFF
            if (unsigned < maxUnbiased) {
                chars.append(alphabet[unsigned % 26])
            }
        }
    }
    return chars.toString()
}
