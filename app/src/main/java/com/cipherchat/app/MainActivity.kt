package com.cipherchat.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cipherchat.app.ui.CipherChatApp
import com.cipherchat.app.ui.theme.CipherChatTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CipherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            CipherChatTheme {
                CipherChatApp(
                    state = state,
                    onGo = viewModel::go,
                    onEncryptMessage = viewModel::setEncryptMessage,
                    onEncryptShift = viewModel::setEncryptShift,
                    onEncryptKey = viewModel::setEncryptKey,
                    onToggleEncryptKey = viewModel::toggleEncryptKeyVisible,
                    onEncryptKeyLength = viewModel::setEncryptKeyLength,
                    onGenerateEncryptKey = viewModel::generateEncryptKey,
                    onClearEncryptKey = viewModel::clearEncryptKey,
                    onUseSample = viewModel::useSampleMessage,
                    onEncrypt = viewModel::encrypt,
                    onDecryptMessage = viewModel::setDecryptMessage,
                    onDecryptShift = viewModel::setDecryptShift,
                    onDecryptKey = viewModel::setDecryptKey,
                    onToggleDecryptKey = viewModel::toggleDecryptKeyVisible,
                    onDecryptKeyLength = viewModel::setDecryptKeyLength,
                    onGenerateDecryptKey = viewModel::generateDecryptKey,
                    onClearDecryptKey = viewModel::clearDecryptKey,
                    onDecrypt = viewModel::decrypt,
                    onCopy = { text, toast ->
                        copyText(text)
                        viewModel.showSnackbar(toast)
                    },
                    onShare = ::shareText,
                    onOpenHistory = viewModel::openHistory,
                    onClearHistory = viewModel::clearHistory,
                    onSelfTest = viewModel::runSelfTest,
                    onSnackbarShown = viewModel::consumeSnackbar
                )
            }
        }
    }

    private fun copyText(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Cipher Chat", text))
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Cipher Chat message")
        }
        startActivity(Intent.createChooser(intent, "Share"))
    }
}
