package com.app.pokeapp.data.music

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class MusicTrack(
    val uri: String,
    val title: String
)

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("music_playlist", Context.MODE_PRIVATE)

    private val _tracks = MutableStateFlow(loadTracks())
    val tracks: StateFlow<List<MusicTrack>> = _tracks.asStateFlow()

    fun addTrack(uri: Uri, title: String) {
        // Take persistable permission so URI survives reboot
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {}

        val current = _tracks.value.toMutableList()
        if (current.none { it.uri == uri.toString() }) {
            current.add(MusicTrack(uri.toString(), title))
            _tracks.value = current
            saveTracks(current)
        }
    }

    fun removeTrack(track: MusicTrack) {
        val current = _tracks.value.toMutableList()
        current.removeAll { it.uri == track.uri }
        _tracks.value = current
        saveTracks(current)
    }

    fun moveTrack(fromIndex: Int, toIndex: Int) {
        val current = _tracks.value.toMutableList()
        if (fromIndex in current.indices && toIndex in current.indices) {
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            _tracks.value = current
            saveTracks(current)
        }
    }

    private fun loadTracks(): List<MusicTrack> {
        val json = prefs.getString("tracks", null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                MusicTrack(obj.getString("uri"), obj.getString("title"))
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun saveTracks(tracks: List<MusicTrack>) {
        val arr = JSONArray()
        tracks.forEach { track ->
            arr.put(JSONObject().apply {
                put("uri", track.uri)
                put("title", track.title)
            })
        }
        prefs.edit().putString("tracks", arr.toString()).apply()
    }
}
