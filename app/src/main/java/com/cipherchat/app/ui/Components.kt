package com.cipherchat.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cipherchat.app.crypto.RANDOM_KEY_LENGTHS
import com.cipherchat.app.crypto.getKeyValidation
import com.cipherchat.app.ui.theme.Cyan
import com.cipherchat.app.ui.theme.Muted

@Composable
fun SecretKeyManager(
    key: String,
    visible: Boolean,
    length: Int,
    onKeyChange: (String) -> Unit,
    onToggleVisible: () -> Unit,
    onLengthChange: (Int) -> Unit,
    onGenerate: () -> Unit,
    onCopy: () -> Unit,
    onClear: () -> Unit
) {
    val validation = getKeyValidation(key)
    OutlinedTextField(
        value = key,
        onValueChange = onKeyChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Example: FRIEZA") },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        trailingIcon = {
            TextButton(onClick = onToggleVisible) {
                Text(if (visible) "Hide Key" else "Show Key")
            }
        }
    )
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Length $length", color = Muted)
        RANDOM_KEY_LENGTHS.forEach { option ->
            TextButton(onClick = { onLengthChange(option) }) {
                Text(option.toString(), color = if (option == length) Cyan else Muted)
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = onGenerate) { Text("Generate Random Key") }
        OutlinedButton(onClick = onCopy, enabled = key.isNotEmpty()) { Text("Copy Key") }
        OutlinedButton(onClick = onClear) { Text("Clear") }
    }
    Spacer(Modifier.height(8.dp))
    Text(
        text = when {
            validation.error.isNotEmpty() -> validation.error
            validation.warning.isNotEmpty() -> validation.warning
            else -> "The same key must be used to encrypt and decrypt. It is never added to the shared message."
        },
        color = Muted
    )
}

@Composable
fun ShiftSlider(shift: Int, onChange: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", color = Muted)
            Text("Current Shift: $shift", color = Cyan)
            Text("25", color = Muted)
        }
        Slider(
            value = shift.toFloat(),
            onValueChange = { onChange(it.toInt().coerceIn(1, 25)) },
            valueRange = 1f..25f,
            steps = 23
        )
    }
}

@Composable
fun PrimaryAction(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(label)
    }
}
