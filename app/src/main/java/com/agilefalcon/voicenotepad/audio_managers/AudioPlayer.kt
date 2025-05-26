package com.agilefalcon.voicenotepad.audio_managers

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast

class AudioPlayer(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false
    private var onCompletionCallback: (() -> Unit)? = null

    fun setPlaybackFinishedCallback(callback: () -> Unit) {
        onCompletionCallback = callback
    }

    fun play(filePath: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.apply {
                setDataSource(filePath)
                prepare()
                mediaPlayer?.setOnCompletionListener {
                    isPaused = false
                    onCompletionCallback?.invoke()
                }
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Çalınamadı", Toast.LENGTH_SHORT).show()
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 1
    }


    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPaused = true
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (isPaused) {
                it.start()
                isPaused = false
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}


