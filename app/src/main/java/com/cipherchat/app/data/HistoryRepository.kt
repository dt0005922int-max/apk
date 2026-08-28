package com.cipherchat.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("cipher_chat_history")

data class HistoryEntry(
    val id: String,
    val type: String,
    val timestamp: Long,
    val packaged: String,
    val preview: String,
    val shift: Int
)

class HistoryRepository(private val context: Context) {
    private val historyKey = stringPreferencesKey("history_json")

    val history: Flow<List<HistoryEntry>> = context.dataStore.data.map { prefs ->
        prefs[historyKey]?.let { decode(it) } ?: emptyList()
    }

    suspend fun add(type: String, packaged: String, preview: String, shift: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[historyKey]?.let { decode(it) } ?: emptyList()
            val next = listOf(
                HistoryEntry(
                    id = "${System.currentTimeMillis()}-${current.size}",
                    type = type,
                    timestamp = System.currentTimeMillis(),
                    packaged = packaged,
                    preview = preview.take(80),
                    shift = shift
                )
            ) + current
            prefs[historyKey] = encode(next.take(40))
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(historyKey)
        }
    }

    private fun encode(items: List<HistoryEntry>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("type", item.type)
                    .put("timestamp", item.timestamp)
                    .put("packaged", item.packaged)
                    .put("preview", item.preview)
                    .put("shift", item.shift)
            )
        }
        return array.toString()
    }

    private fun decode(raw: String): List<HistoryEntry> {
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    HistoryEntry(
                        id = obj.getString("id"),
                        type = obj.optString("type"),
                        timestamp = obj.optLong("timestamp"),
                        packaged = obj.optString("packaged"),
                        preview = obj.optString("preview"),
                        shift = obj.optInt("shift")
                    )
                )
            }
        }
    }
}
