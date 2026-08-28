package com.cipherchat.app.crypto

data class EncryptResult(
    val originalMessage: String,
    val shift: Int,
    val caesarOutput: String,
    val vigenereOutput: String,
    val packaged: String,
    val verified: Boolean
)

data class DecryptResult(
    val parsed: ParsedMessage,
    val shift: Int,
    val vigenereOutput: String,
    val originalMessage: String,
    val structureVerified: Boolean
)

fun encryptMessage(originalMessage: String, shift: Int, secretKey: String): EncryptResult {
    require(originalMessage.isNotEmpty()) { "Enter a message to encrypt." }
    require(isValidShift(shift)) { "Caesar shift must be an integer from 1 to 25." }

    val caesarOutput = caesarEncrypt(originalMessage, shift)
    val normalizedKey = normalizeVigenereKey(secretKey)
    val vigenereOutput = vigenereEncrypt(caesarOutput, normalizedKey)
    val recoveredVigenere = vigenereDecrypt(vigenereOutput, normalizedKey)
    val recoveredOriginal = caesarDecrypt(recoveredVigenere, shift)
    return EncryptResult(
        originalMessage = originalMessage,
        shift = shift,
        caesarOutput = caesarOutput,
        vigenereOutput = vigenereOutput,
        packaged = vigenereOutput,
        verified = recoveredOriginal == originalMessage
    )
}

fun decryptMessage(message: String, secretKey: String, manualShift: Int): DecryptResult {
    require(message.isNotEmpty()) { "Enter an encrypted message to decrypt." }
    val parsed = parseEncryptedPackage(message)
    val shift = if (parsed.isFormatted) {
        val detected = parsed.shift
        require(detected != null && isValidShift(detected)) {
            "This message has an invalid Caesar shift. Shift must be from 1 to 25."
        }
        detected
    } else {
        require(isValidShift(manualShift)) { "Enter a Caesar shift from 1 to 25." }
        manualShift
    }

    val normalizedKey = normalizeVigenereKey(secretKey)
    val vigenereOutput = vigenereDecrypt(parsed.encryptedText, normalizedKey)
    val originalMessage = caesarDecrypt(vigenereOutput, shift)
    val reEncrypted = vigenereEncrypt(caesarEncrypt(originalMessage, shift), normalizedKey)
    return DecryptResult(
        parsed = parsed,
        shift = shift,
        vigenereOutput = vigenereOutput,
        originalMessage = originalMessage,
        structureVerified = reEncrypted == parsed.encryptedText
    )
}

fun runReversibilityChecks(): Boolean {
    val samples = listOf(
        "I will meet you tomorrow at 8 PM.",
        "HELLO",
        "Hello, World! 123",
        "Wrap Zz to Aa."
    )
    return samples.all { sample ->
        val encrypted = encryptMessage(sample, 3, "FRIEZA")
        val decrypted = decryptMessage(encrypted.packaged, "FRIEZA", 3)
        encrypted.verified && decrypted.originalMessage == sample
    }
}
