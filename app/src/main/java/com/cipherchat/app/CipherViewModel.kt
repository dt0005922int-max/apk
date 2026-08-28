package com.cipherchat.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cipherchat.app.crypto.DecryptResult
import com.cipherchat.app.crypto.EncryptResult
import com.cipherchat.app.crypto.DEFAULT_KEY_LENGTH
import com.cipherchat.app.crypto.decryptMessage
import com.cipherchat.app.crypto.encryptMessage
import com.cipherchat.app.crypto.generateRandomKey
import com.cipherchat.app.crypto.isValidShift
import com.cipherchat.app.crypto.parseEncryptedPackage
import com.cipherchat.app.crypto.runReversibilityChecks
import com.cipherchat.app.data.HistoryEntry
import com.cipherchat.app.data.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen { Home, Encrypt, Decrypt, Guide, History }

data class CipherUiState(
    val screen: Screen = Screen.Home,
    val encryptMessage: String = "",
    val encryptShift: Int = 3,
    val encryptKey: String = "",
    val encryptKeyVisible: Boolean = false,
    val encryptKeyLength: Int = DEFAULT_KEY_LENGTH,
    val encryptResult: EncryptResult? = null,
    val encryptError: String? = null,
    val decryptMessage: String = "",
    val decryptShift: Int = 3,
    val decryptKey: String = "",
    val decryptKeyVisible: Boolean = false,
    val decryptKeyLength: Int = DEFAULT_KEY_LENGTH,
    val decryptResult: DecryptResult? = null,
    val decryptError: String? = null,
    val detailsOpen: Boolean = false,
    val history: List<HistoryEntry> = emptyList(),
    val snackbar: String? = null,
    val selfTestPassed: Boolean? = null
)

class CipherViewModel(application: Application) : AndroidViewModel(application) {
    private val historyRepository = HistoryRepository(application)

    private val _state = MutableStateFlow(CipherUiState())
    val state: StateFlow<CipherUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            historyRepository.history.collect { entries ->
                _state.update { it.copy(history = entries) }
            }
        }
    }

    fun go(screen: Screen) {
        _state.update { it.copy(screen = screen, snackbar = null) }
    }

    fun setEncryptMessage(value: String) {
        _state.update { it.copy(encryptMessage = value, encryptResult = null, encryptError = null) }
    }

    fun setEncryptShift(value: Int) {
        _state.update { it.copy(encryptShift = value.coerceIn(1, 25), encryptResult = null) }
    }

    fun setEncryptKey(value: String) {
        _state.update { it.copy(encryptKey = value, encryptResult = null, encryptError = null) }
    }

    fun toggleEncryptKeyVisible() {
        _state.update { it.copy(encryptKeyVisible = !it.encryptKeyVisible) }
    }

    fun setEncryptKeyLength(value: Int) {
        _state.update { it.copy(encryptKeyLength = value) }
    }

    fun generateEncryptKey() {
        _state.update {
            it.copy(
                encryptKey = generateRandomKey(it.encryptKeyLength),
                encryptResult = null,
                encryptError = null
            )
        }
    }

    fun clearEncryptKey() {
        _state.update { it.copy(encryptKey = "", encryptResult = null) }
    }

    fun useSampleMessage() {
        _state.update {
            it.copy(
                screen = Screen.Encrypt,
                encryptMessage = "I will meet you tomorrow at 8 PM.",
                encryptResult = null,
                encryptError = null
            )
        }
    }

    fun encrypt() {
        val current = _state.value
        try {
            val output = encryptMessage(current.encryptMessage, current.encryptShift, current.encryptKey)
            if (!output.verified) {
                _state.update {
                    it.copy(
                        encryptResult = null,
                        encryptError = "Verification Failed. The recovered message did not match the original."
                    )
                }
                return
            }
            _state.update { it.copy(encryptResult = output, encryptError = null, detailsOpen = false) }
            viewModelScope.launch {
                historyRepository.add("encrypt", output.packaged, output.originalMessage, output.shift)
            }
        } catch (error: Exception) {
            _state.update { it.copy(encryptResult = null, encryptError = error.message) }
        }
    }

    fun setDecryptMessage(value: String) {
        val parsed = parseEncryptedPackage(value)
        val shift = if (parsed.isFormatted && parsed.shift != null && isValidShift(parsed.shift)) {
            parsed.shift
        } else {
            _state.value.decryptShift
        }
        _state.update {
            it.copy(
                decryptMessage = value,
                decryptShift = shift,
                decryptResult = null,
                decryptError = null
            )
        }
    }

    fun setDecryptShift(value: Int) {
        _state.update { it.copy(decryptShift = value.coerceIn(1, 25), decryptResult = null) }
    }

    fun setDecryptKey(value: String) {
        _state.update { it.copy(decryptKey = value, decryptResult = null, decryptError = null) }
    }

    fun toggleDecryptKeyVisible() {
        _state.update { it.copy(decryptKeyVisible = !it.decryptKeyVisible) }
    }

    fun setDecryptKeyLength(value: Int) {
        _state.update { it.copy(decryptKeyLength = value) }
    }

    fun generateDecryptKey() {
        _state.update { it.copy(decryptKey = generateRandomKey(it.decryptKeyLength), decryptResult = null) }
    }

    fun clearDecryptKey() {
        _state.update { it.copy(decryptKey = "", decryptResult = null) }
    }

    fun decrypt() {
        val current = _state.value
        try {
            val output = decryptMessage(current.decryptMessage, current.decryptKey, current.decryptShift)
            _state.update {
                it.copy(
                    decryptResult = output,
                    decryptError = null,
                    decryptShift = output.shift,
                    detailsOpen = false
                )
            }
            viewModelScope.launch {
                historyRepository.add(
                    "decrypt",
                    current.decryptMessage.trim(),
                    output.originalMessage,
                    output.shift
                )
            }
        } catch (error: Exception) {
            _state.update { it.copy(decryptResult = null, decryptError = error.message) }
        }
    }

    fun openHistory(entry: HistoryEntry) {
        _state.update {
            it.copy(
                screen = Screen.Decrypt,
                decryptMessage = entry.packaged,
                decryptShift = entry.shift,
                decryptResult = null,
                decryptError = null
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clear() }
    }

    fun runSelfTest() {
        _state.update { it.copy(selfTestPassed = runReversibilityChecks()) }
    }

    fun showSnackbar(message: String) {
        _state.update { it.copy(snackbar = message) }
    }

    fun consumeSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}
