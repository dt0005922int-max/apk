package com.cipherchat.app.crypto

const val MESSAGE_VERSION = "CC1"

data class ParsedMessage(
    val version: String,
    val shift: Int?,
    val encryptedText: String,
    val isFormatted: Boolean
)

fun createEncryptedPackage(encryptedText: String, shift: Int): String {
    return "$MESSAGE_VERSION|S$shift|$encryptedText"
}

fun parseEncryptedPackage(message: String): ParsedMessage {
    val trimmed = message.trim().removePrefix("\uFEFF")
    if (trimmed.startsWith("$MESSAGE_VERSION|")) {
        val afterVersion = trimmed.substring(MESSAGE_VERSION.length + 1)
        val match = Regex("^S(\\d+)\\|").find(afterVersion)
        if (match != null) {
            val shift = match.groupValues[1].toIntOrNull()
            val encryptedText = afterVersion.substring(match.value.length)
            return ParsedMessage(
                version = MESSAGE_VERSION,
                shift = shift,
                encryptedText = encryptedText,
                isFormatted = true
            )
        }
    }
    return ParsedMessage(
        version = "",
        shift = null,
        encryptedText = message,
        isFormatted = false
    )
}

fun isValidShift(shift: Int): Boolean = shift in 1..25
