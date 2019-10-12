package com.kamranmackey.pulse.backend.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException
import java.lang.NullPointerException

class PlayerService: Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnInfoListener, AudioManager.OnAudioFocusChangeListener {

    private val tag = this.javaClass.simpleName

    private lateinit var audioManager: AudioManager
    private lateinit var player: MediaPlayer

    private var mediaFile: String? = null

    private val binder = PlayerBinder()

    private var resumePosition: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }


    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        // not yet implemented
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopMediaPlayback()
        stopSelf()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d(tag, "Media error not valid for progressive playback $extra")
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                Log.d(tag, "Media error server died $extra")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.d(tag, "Media error unknown $extra")
            }
        }
        return false
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        beginMediaPlayback()
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        // not yet implemented
    }

    override fun onAudioFocusChange(focusChange: Int) {
       when (focusChange) {
           AudioManager.AUDIOFOCUS_GAIN -> {
               if (!player.isPlaying) player.start()
               player.setVolume(1.0f, 1.0f)
           }
           AudioManager.AUDIOFOCUS_LOSS -> {
               if (player.isPlaying) player.stop()
               player.release()
           }
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
               if (player.isPlaying) player.pause()
           }
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
               if (player.isPlaying) player.setVolume(0.1f, 0.1f)
           }
       }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result: Int = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {
            mediaFile = intent?.extras?.getString("media")
        } catch (e: NullPointerException) {
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this)
    }

    private fun initializeMediaPlayer() {
        player = MediaPlayer()
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        player.setOnPreparedListener(this)
        player.setOnBufferingUpdateListener(this)
        player.setOnCompletionListener(this)
        player.setOnInfoListener(this)
        player.reset()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        try {
            player.setDataSource(mediaFile)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }

        player.prepareAsync()
    }

    private fun beginMediaPlayback() {
        if (!player.isPlaying) {
            player.start()
        }
    }

    private fun stopMediaPlayback() {
        if (player.isPlaying) {
            player.stop()
        }
    }

    private fun pauseMediaPlayback() {
        if (player.isPlaying) {
            player.pause()
            resumePosition = player.currentPosition
        }
    }

    private fun resumeMediaPlayback() {
        if (!player.isPlaying) {
            player.seekTo(resumePosition)
            player.start()
        }
    }

    inner class PlayerBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

}