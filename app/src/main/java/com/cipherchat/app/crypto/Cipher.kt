package com.cipherchat.app.crypto

private const val AlphaLen = 26

fun caesarEncrypt(text: String, shift: Int): String = applyCaesar(text, shift)

fun caesarDecrypt(text: String, shift: Int): String = applyCaesar(text, -shift)

private fun applyCaesar(text: String, shift: Int): String {
    val normalized = ((shift % AlphaLen) + AlphaLen) % AlphaLen
    return buildString(text.length) {
        for (ch in text) {
            append(shiftLatinLetter(ch, normalized))
        }
    }
}

private fun shiftLatinLetter(ch: Char, shift: Int): Char {
    val code = ch.code
    return when {
        code in 65..90 -> ((code - 65 + shift) % AlphaLen + 65).toChar()
        code in 97..122 -> ((code - 97 + shift) % AlphaLen + 97).toChar()
        else -> ch
    }
}

fun vigenereEncrypt(text: String, key: String): String = vigenereTransform(text, key, 1)

fun vigenereDecrypt(text: String, key: String): String = vigenereTransform(text, key, -1)

private fun vigenereTransform(text: String, key: String, direction: Int): String {
    require(key.isNotEmpty() && key.all { it in 'A'..'Z' }) {
        "Vigenère key must be normalized to uppercase A–Z letters only."
    }
    var keyIndex = 0
    return buildString(text.length) {
        for (ch in text) {
            val code = ch.code
            val base = when {
                code in 65..90 -> 65
                code in 97..122 -> 97
                else -> -1
            }
            if (base == -1) {
                append(ch)
                continue
            }
            val keyShift = key[keyIndex % key.length].code - 65
            val shifted = ((code - base + direction * keyShift) % AlphaLen + AlphaLen) % AlphaLen
            append((base + shifted).toChar())
            keyIndex += 1
        }
    }
}
