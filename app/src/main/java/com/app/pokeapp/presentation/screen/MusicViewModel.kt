package com.app.pokeapp.presentation.screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import com.app.pokeapp.data.music.MusicRepository
import com.app.pokeapp.data.music.MusicTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MusicUiState(
    val currentTrackIndex: Int = -1,
    val isPlaying: Boolean = false
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val tracks: StateFlow<List<MusicTrack>> = musicRepository.tracks

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    fun addTrack(uri: Uri) {
        val title = getFileName(uri) ?: "Traccia sconosciuta"
        musicRepository.addTrack(uri, title)
    }

    fun removeTrack(track: MusicTrack) {
        val currentIndex = _uiState.value.currentTrackIndex
        val trackIndex = tracks.value.indexOf(track)

        if (trackIndex == currentIndex) {
            stop()
        } else if (trackIndex < currentIndex) {
            _uiState.update { it.copy(currentTrackIndex = currentIndex - 1) }
        }

        musicRepository.removeTrack(track)
    }

    fun playTrack(index: Int) {
        val trackList = tracks.value
        if (index !in trackList.indices) return

        releasePlayer()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(trackList[index].uri))
                prepare()
                start()
                setOnCompletionListener { playNext() }
            }
            _uiState.update { it.copy(currentTrackIndex = index, isPlaying = true) }
        } catch (_: Exception) {
            _uiState.update { it.copy(isPlaying = false) }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player == null) {
            if (tracks.value.isNotEmpty()) {
                val index = if (_uiState.value.currentTrackIndex >= 0)
                    _uiState.value.currentTrackIndex else 0
                playTrack(index)
            }
            return
        }

        if (player.isPlaying) {
            player.pause()
            _uiState.update { it.copy(isPlaying = false) }
        } else {
            player.start()
            _uiState.update { it.copy(isPlaying = true) }
        }
    }

    fun playNext() {
        val trackList = tracks.value
        if (trackList.isEmpty()) return
        val nextIndex = (_uiState.value.currentTrackIndex + 1) % trackList.size
        playTrack(nextIndex)
    }

    fun playPrevious() {
        val trackList = tracks.value
        if (trackList.isEmpty()) return
        val currentIndex = _uiState.value.currentTrackIndex
        val prevIndex = if (currentIndex <= 0) trackList.size - 1 else currentIndex - 1
        playTrack(prevIndex)
    }

    fun stop() {
        releasePlayer()
        _uiState.update { it.copy(isPlaying = false, currentTrackIndex = -1) }
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun getFileName(uri: Uri): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)?.substringBeforeLast('.')
            }
        } catch (_: Exception) { null }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
