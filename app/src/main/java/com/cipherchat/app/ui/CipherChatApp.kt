package com.cipherchat.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cipherchat.app.CipherUiState
import com.cipherchat.app.Screen
import com.cipherchat.app.crypto.getKeyValidation
import com.cipherchat.app.crypto.isValidShift
import com.cipherchat.app.crypto.parseEncryptedPackage
import com.cipherchat.app.ui.theme.Bg
import com.cipherchat.app.ui.theme.Cyan
import com.cipherchat.app.ui.theme.Danger
import com.cipherchat.app.ui.theme.Ink
import com.cipherchat.app.ui.theme.Muted
import com.cipherchat.app.ui.theme.Ok
import com.cipherchat.app.ui.theme.Surface
import com.cipherchat.app.ui.theme.Warn
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherChatApp(
    state: CipherUiState,
    onGo: (Screen) -> Unit,
    onEncryptMessage: (String) -> Unit,
    onEncryptShift: (Int) -> Unit,
    onEncryptKey: (String) -> Unit,
    onToggleEncryptKey: () -> Unit,
    onEncryptKeyLength: (Int) -> Unit,
    onGenerateEncryptKey: () -> Unit,
    onClearEncryptKey: () -> Unit,
    onUseSample: () -> Unit,
    onEncrypt: () -> Unit,
    onDecryptMessage: (String) -> Unit,
    onDecryptShift: (Int) -> Unit,
    onDecryptKey: (String) -> Unit,
    onToggleDecryptKey: () -> Unit,
    onDecryptKeyLength: (Int) -> Unit,
    onGenerateDecryptKey: () -> Unit,
    onClearDecryptKey: () -> Unit,
    onDecrypt: () -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: (String) -> Unit,
    onOpenHistory: (com.cipherchat.app.data.HistoryEntry) -> Unit,
    onClearHistory: () -> Unit,
    onSelfTest: () -> Unit,
    onSnackbarShown: () -> Unit
) {
    val snackbarHost = remember { SnackbarHostState() }
    LaunchedEffect(state.snackbar) {
        val message = state.snackbar ?: return@LaunchedEffect
        snackbarHost.showSnackbar(message)
        onSnackbarShown()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CIPHER CHAT", fontWeight = FontWeight.Bold)
                        Text("Encrypt • Share • Decrypt", color = Cyan, style = MaterialTheme.typography.labelMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Bg,
                    titleContentColor = Ink
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(state.screen == Screen.Home, { onGo(Screen.Home) }, { Icon(Icons.Outlined.Home, null) }, label = { Text("Home") })
                NavigationBarItem(state.screen == Screen.Encrypt, { onGo(Screen.Encrypt) }, { Icon(Icons.Outlined.Lock, null) }, label = { Text("Encrypt") })
                NavigationBarItem(state.screen == Screen.Decrypt, { onGo(Screen.Decrypt) }, { Icon(Icons.Outlined.LockOpen, null) }, label = { Text("Decrypt") })
                NavigationBarItem(state.screen == Screen.Guide, { onGo(Screen.Guide) }, { Icon(Icons.Outlined.Info, null) }, label = { Text("Guide") })
                NavigationBarItem(state.screen == Screen.History, { onGo(Screen.History) }, { Icon(Icons.Outlined.History, null) }, label = { Text("History") })
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = Bg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    when (state.screen) {
                    Screen.Home -> HomePane(onGo, onUseSample)
                    Screen.Encrypt -> EncryptPane(
                        state = state,
                        onMessage = onEncryptMessage,
                        onShift = onEncryptShift,
                        onKey = onEncryptKey,
                        onToggleKey = onToggleEncryptKey,
                        onLength = onEncryptKeyLength,
                        onGenerate = onGenerateEncryptKey,
                        onCopyKey = { onCopy(state.encryptKey, "Key copied") },
                        onClearKey = onClearEncryptKey,
                        onSample = onUseSample,
                        onEncrypt = onEncrypt,
                        onCopyPackage = { state.encryptResult?.let { onCopy(it.packaged, "Encrypted message copied") } },
                        onShare = { state.encryptResult?.let { onShare(it.packaged) } }
                    )
                    Screen.Decrypt -> DecryptPane(
                        state = state,
                        onMessage = onDecryptMessage,
                        onShift = onDecryptShift,
                        onKey = onDecryptKey,
                        onToggleKey = onToggleDecryptKey,
                        onLength = onDecryptKeyLength,
                        onGenerate = onGenerateDecryptKey,
                        onCopyKey = { onCopy(state.decryptKey, "Key copied") },
                        onClearKey = onClearDecryptKey,
                        onDecrypt = onDecrypt,
                        onCopyPlain = { state.decryptResult?.let { onCopy(it.originalMessage, "Original message copied") } },
                        onShare = { state.decryptResult?.let { onShare(it.originalMessage) } }
                    )
                    Screen.Guide -> GuidePane(state.selfTestPassed, onSelfTest)
                    Screen.History -> HistoryPane(state, onOpenHistory, onCopy, onClearHistory)
                    }
                }
            }
        }
    }
}

@Composable
private fun Panel(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = { content() })
    }
}

@Composable
private fun StepLabel(step: String, title: String) {
    Column {
        Text(step, color = Cyan, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SecurityBanner() {
    Panel {
        Text("Security warning", color = Warn, fontWeight = FontWeight.Bold)
        Text(
            "Caesar and Vigenère are classical ciphers and are not secure against modern cryptographic attacks. Do not use this app for passwords, banking information, financial data, or highly sensitive information.",
            color = Muted
        )
        Text("This app is for learning, fun, puzzles, and casual private messages. Everything stays on this phone.", color = Muted)
    }
}

@Composable
private fun HomePane(onGo: (Screen) -> Unit, onUseSample: () -> Unit) {
    Text("CIPHER CHAT", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
    Text("Encrypt messages. Share them. Only someone with the shared key can decode them.", color = Muted)
    PrimaryAction("Encrypt a Message", true) { onGo(Screen.Encrypt) }
    OutlinedButton(onClick = { onGo(Screen.Decrypt) }, modifier = Modifier.fillMaxWidth()) { Text("Decrypt a Message") }
    OutlinedButton(onClick = onUseSample, modifier = Modifier.fillMaxWidth()) { Text("Use sample message") }
    Panel {
        Text("PIPELINE", color = Cyan, style = MaterialTheme.typography.labelSmall)
        Text("Original English message → Caesar cipher → Vigenère encryption → encrypted message")
        Text("decrypt(encrypt(originalMessage)) === originalMessage, including uppercase, lowercase, spaces, punctuation, and numbers.", color = Muted)
    }
    SecurityBanner()
}

@Composable
private fun EncryptPane(
    state: CipherUiState,
    onMessage: (String) -> Unit,
    onShift: (Int) -> Unit,
    onKey: (String) -> Unit,
    onToggleKey: () -> Unit,
    onLength: (Int) -> Unit,
    onGenerate: () -> Unit,
    onCopyKey: () -> Unit,
    onClearKey: () -> Unit,
    onSample: () -> Unit,
    onEncrypt: () -> Unit,
    onCopyPackage: () -> Unit,
    onShare: () -> Unit
) {
    Text("Person A", color = Cyan, style = MaterialTheme.typography.labelSmall)
    Text("Encrypt a Message", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Panel {
        StepLabel("STEP 01", "Enter Your Message")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onSample) { Text("Use sample") }
            OutlinedButton(onClick = { onMessage("") }) { Text("Clear") }
        }
        OutlinedTextField(
            value = state.encryptMessage,
            onValueChange = onMessage,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = { Text("Type your message here...") },
            shape = RoundedCornerShape(14.dp)
        )
        Text("${state.encryptMessage.length} characters", color = Muted)
    }
    Panel {
        StepLabel("STEP 02", "Caesar Shift")
        ShiftSlider(state.encryptShift, onShift)
    }
    Panel {
        StepLabel("STEP 03", "Shared Secret Key")
        SecretKeyManager(
            key = state.encryptKey,
            visible = state.encryptKeyVisible,
            length = state.encryptKeyLength,
            onKeyChange = onKey,
            onToggleVisible = onToggleKey,
            onLengthChange = onLength,
            onGenerate = onGenerate,
            onCopy = onCopyKey,
            onClear = onClearKey
        )
    }
    Panel {
        StepLabel("STEP 04", "Encrypt")
        PrimaryAction(
            "Encrypt Message",
            state.encryptMessage.isNotBlank() && getKeyValidation(state.encryptKey).valid,
            onEncrypt
        )
    }
    state.encryptError?.let {
        Panel { Text(it, color = Danger, fontWeight = FontWeight.Bold) }
    }
    state.encryptResult?.let { result ->
        Panel {
            StepLabel("RESULT", "Encrypted Message")
            Text("✓ Encryption Verified Successfully", color = Ok, fontWeight = FontWeight.Bold)
            Text("This is what Person A sends to Person B. Share the Caesar shift (${result.shift}) and the secret key separately.", color = Muted)
            Text(result.packaged, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCopyPackage) { Text("Copy Message") }
                OutlinedButton(onClick = onShare) { Text("Share") }
            }
            Text("Original: ${result.originalMessage}", color = Muted)
            Text("Caesar shift: ${result.shift}", color = Muted)
            Text("Caesar output: ${result.caesarOutput}", fontFamily = FontFamily.Monospace, color = Muted)
            Text("Vigenère output: ${result.vigenereOutput}", fontFamily = FontFamily.Monospace, color = Muted)
            Text("Secret key: ${if (state.encryptKeyVisible) state.encryptKey else "Hidden — tap Show Key"}", color = Muted)
        }
    }
    SecurityBanner()
}

@Composable
private fun DecryptPane(
    state: CipherUiState,
    onMessage: (String) -> Unit,
    onShift: (Int) -> Unit,
    onKey: (String) -> Unit,
    onToggleKey: () -> Unit,
    onLength: (Int) -> Unit,
    onGenerate: () -> Unit,
    onCopyKey: () -> Unit,
    onClearKey: () -> Unit,
    onDecrypt: () -> Unit,
    onCopyPlain: () -> Unit,
    onShare: () -> Unit
) {
    val parsed = parseEncryptedPackage(state.decryptMessage)
    val formatted = parsed.isFormatted && parsed.shift != null && isValidShift(parsed.shift)
    Text("Person B", color = Cyan, style = MaterialTheme.typography.labelSmall)
    Text("Decrypt a Message", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Panel {
        StepLabel("STEP 01", "Encrypted Message")
        OutlinedButton(onClick = { onMessage("") }) { Text("Clear") }
        OutlinedTextField(
            value = state.decryptMessage,
            onValueChange = onMessage,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = { Text("Paste the encrypted message here...") },
            shape = RoundedCornerShape(14.dp)
        )
        Text("${state.decryptMessage.length} characters", color = Muted)
        when {
            formatted -> Text("Old Cipher Chat format detected. Caesar shift set to ${parsed.shift} automatically.", color = Cyan)
            state.decryptMessage.isNotBlank() -> Text("Set the Caesar shift below to the same value Person A used.", color = Muted)
            else -> Text("Paste the encrypted message you received.", color = Muted)
        }
    }
    Panel {
        if (formatted) {
            StepLabel("STEP 02", "Caesar Shift")
            Text("Current Shift: ${parsed.shift} (from message format)", color = Cyan)
        } else {
            StepLabel("STEP 02", "Caesar Shift")
            ShiftSlider(state.decryptShift, onShift)
        }
    }
    Panel {
        StepLabel("STEP 03", "Shared Secret Key")
        SecretKeyManager(
            key = state.decryptKey,
            visible = state.decryptKeyVisible,
            length = state.decryptKeyLength,
            onKeyChange = onKey,
            onToggleVisible = onToggleKey,
            onLengthChange = onLength,
            onGenerate = onGenerate,
            onCopy = onCopyKey,
            onClear = onClearKey
        )
    }
    PrimaryAction(
        "Decrypt Message",
        state.decryptMessage.isNotEmpty() && getKeyValidation(state.decryptKey).valid &&
            (formatted || isValidShift(state.decryptShift)),
        onDecrypt
    )
    state.decryptError?.let {
        Panel { Text(it, color = Danger, fontWeight = FontWeight.Bold) }
    }
    state.decryptResult?.let { result ->
        Panel {
            StepLabel("RESULT", "Original Message")
            Text("Decryption Successful", color = Ok, fontWeight = FontWeight.Bold)
            Text(result.originalMessage, fontFamily = FontFamily.Monospace)
            if (result.structureVerified) {
                Text("✓ Decryption Structure Verified. A wrong Vigenère key can sometimes still produce structurally valid text. This check does not prove that the key is correct.", color = Ok)
            } else {
                Text("The recovered text did not re-encrypt to the exact same ciphertext. The key may be wrong.", color = Warn)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCopyPlain) { Text("Copy") }
                OutlinedButton(onClick = onShare) { Text("Share") }
            }
        }
    }
    SecurityBanner()
}

@Composable
private fun GuidePane(selfTestPassed: Boolean?, onSelfTest: () -> Unit) {
    Text("How it Works", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Panel {
        Text("Person A", fontWeight = FontWeight.Bold)
        Text("1. Writes a message in English.\n2. Chooses a Caesar shift and a Vigenère secret key.\n3. Copies the encrypted message and sends it through WhatsApp, Messenger, Instagram, or email.\n4. Shares the shift and the secret key with Person B separately, not in the same message.")
    }
    Panel {
        Text("Person B", fontWeight = FontWeight.Bold)
        Text("1. Pastes the encrypted message into Decrypt.\n2. Sets the same Caesar shift and enters the same secret key.\n3. Reads the original English message.")
    }
    Panel {
        Text("Worked example", fontWeight = FontWeight.Bold)
        Text("HELLO + shift 3 → KHOOR\nKHOOR + key FRIEZA → PYWSQ\nSend: PYWSQ", fontFamily = FontFamily.Monospace)
        Text("The message reveals nothing about the cipher. Person B needs the shift (3) and the key (FRIEZA), shared separately.", color = Muted)
    }
    Panel {
        Text("Reversibility self-test", fontWeight = FontWeight.Bold)
        OutlinedButton(onClick = onSelfTest) { Text("Run self-test") }
        selfTestPassed?.let {
            Text(
                if (it) "✓ All reversibility checks passed" else "✗ A reversibility check failed",
                color = if (it) Ok else Danger,
                fontWeight = FontWeight.Bold
            )
        }
    }
    SecurityBanner()
}

@Composable
private fun HistoryPane(
    state: CipherUiState,
    onOpen: (com.cipherchat.app.data.HistoryEntry) -> Unit,
    onCopy: (String, String) -> Unit,
    onClear: () -> Unit
) {
    Text("History", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Optional local history. Secret keys are never saved.", color = Muted)
    OutlinedButton(onClick = onClear) { Text("Clear history") }
    if (state.history.isEmpty()) {
        Panel { Text("No local history yet. Secret keys are never stored.", color = Muted) }
    } else {
        state.history.forEach { item ->
            Panel {
                Text(
                    "${if (item.type == "encrypt") "Encrypted" else "Decrypted"} · ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(item.timestamp))} · Shift ${item.shift}",
                    fontWeight = FontWeight.Bold
                )
                Text(item.preview.ifBlank { item.packaged.take(80) }, color = Muted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onOpen(item) }) { Text("Open in Decrypt") }
                    OutlinedButton(onClick = { onCopy(item.packaged, "Package copied") }) { Text("Copy package") }
                }
            }
        }
    }
}
