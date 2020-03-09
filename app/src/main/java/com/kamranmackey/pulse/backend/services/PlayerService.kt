package com.kamranmackey.pulse.backend.services

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer.*
import android.media.session.MediaController.TransportControls
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.Html
import android.util.Log
import com.kamranmackey.pulse.R
import com.kamranmackey.pulse.backend.models.PlaybackStatus
import com.kamranmackey.pulse.backend.models.Song
import com.kamranmackey.pulse.backend.receivers.BecomingNoisyReceiver
import com.kamranmackey.pulse.utils.MusicUtils
import com.kamranmackey.pulse.utils.StorageUtils
import java.io.IOException


class PlayerService : Service(), OnCompletionListener, OnPreparedListener, OnErrorListener,
    OnSeekCompleteListener, OnInfoListener, OnBufferingUpdateListener, OnAudioFocusChangeListener {

    private lateinit var mManager: AudioManager
    private var mPlayer: MediaPlayer? = null

    private val notifId: Int = 101

    val playNewAudioBroadcast = "com.kamranmackey.pulse.newAudioBroadcast"

    // since PlayerService is basically already an AudioFocusChangeListener,
    // just pass this to the variable. This also lets us use the Service
    // in our focusRequest variable with the .run { } syntax.
    private var afChangeListener: OnAudioFocusChangeListener = this

    private val playAction: String = "com.kamranmackey.pulse.ACTION_PLAY"
    private val pauseAction: String = "com.kamranmackey.pulse.ACTION_PAUSE"
    private val prevAction: String = "com.kamranmackey.pulse.ACTION_PREV"
    private val nextAction: String = "com.kamranmackey.pulse.ACTION_NEXT"
    private val stopAction: String = "com.kamranmackey.pulse.ACTION_STOP"

    private var mSessionManager: MediaSessionManager? = null
    private var mSession: MediaSession? = null
    private var mTransportControls: TransportControls? = null

    private val iBinder: IBinder = PlayerBinder()
    private var resumePosition = 0

    private var ongoingCall: Boolean = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    var audioList: ArrayList<Song>? = null
    var audioIndex: Int = -1
    var activeSong: Song? = null

    @TargetApi(Build.VERSION_CODES.O)
    private var focusRequest: AudioFocusRequest = AudioFocusRequest.Builder(
        AudioManager.AUDIOFOCUS_GAIN
    ).run {
        setAudioAttributes(AudioAttributes.Builder().run {
            setUsage(AudioAttributes.USAGE_MEDIA)
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setOnAudioFocusChangeListener(afChangeListener)
            build()
        })
        setAcceptsDelayedFocusGain(true)
        build()
    }

    override fun onBufferingUpdate(player: MediaPlayer, percent: Int) {}

    override fun onCompletion(player: MediaPlayer) {
        skipToNext()
        updateMetadata()
        buildNotification(PlaybackStatus.PLAYING)
    }

    override fun onError(player: MediaPlayer, what: Int, extra: Int): Boolean {
        when (what) {
            MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("MediaPlayer Error", "Progressive playback error: $extra")
            }
            MEDIA_ERROR_SERVER_DIED -> {
                Log.d("MediaPlayer Error", "The server died! More info: $extra")
            }
            MEDIA_ERROR_UNKNOWN -> {
                Log.d("MediaPlayer Error", "Unknown error encountered: $extra")
            }
        }
        return false
    }

    override fun onInfo(player: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onPrepared(player: MediaPlayer) {
        playMedia()
    }

    override fun onSeekComplete(player: MediaPlayer) {}

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mPlayer == null) {
                    initMediaPlayer()
                } else if (!mPlayer!!.isPlaying) {
                    mPlayer!!.start()
                }

                mPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mPlayer!!.isPlaying) {
                    mPlayer!!.stop()
                    mPlayer!!.release()
                    mPlayer = null
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (mPlayer!!.isPlaying) {
                    mPlayer!!.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (mPlayer!!.isPlaying) {
                    mPlayer!!.setVolume(0.1f, 0.1f)
                }
            }
        }
    }

    private val newAudioReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            audioIndex = StorageUtils(applicationContext).loadAudioIndex()
            if (audioIndex != -1 && audioIndex < audioList!!.size) {
                activeSong = audioList!![audioIndex]
            } else {
                stopSelf()
            }

            stopMedia()
            mPlayer!!.reset()
            initMediaPlayer()
            updateMetadata()
            buildNotification(PlaybackStatus.PLAYING)
        }
    }

    private fun requestAudioFocus(): Boolean {
        mManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mManager.requestAudioFocus(focusRequest)
        } else {
            mManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }

        return false
    }

    private fun removeAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mManager.abandonAudioFocusRequest(
                focusRequest
            )
        } else {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mManager.abandonAudioFocus(this)
        }
    }

    fun buildNotification(state: PlaybackStatus) {
        var notificationAction = Icon.createWithResource(applicationContext, R.drawable.ic_pause)
        var playbackIntent: PendingIntent? = null

        if (state == PlaybackStatus.PLAYING) {
            notificationAction = Icon.createWithResource(applicationContext, R.drawable.ic_pause)
            playbackIntent = playbackAction(1)
        } else if (state == PlaybackStatus.PAUSED) {
            notificationAction = Icon.createWithResource(applicationContext, R.drawable.ic_play)
            playbackIntent = playbackAction(0)
        }

        val playPauseAction = Notification.Action.Builder(
            notificationAction,
            "Play / Pause",
            playbackIntent
        ).build()

        val nextSongAction = Notification.Action.Builder(
            Icon.createWithResource(applicationContext, R.drawable.ic_skip_next),
            "Next Song",
            playbackAction(2)
        ).build()

        val prevSongAction = Notification.Action.Builder(
            Icon.createWithResource(applicationContext, R.drawable.ic_skip_previous),
            "Previous Song",
            playbackAction(3)
        ).build()

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "com.kamranmackey.pulse.playback").run {
                setShowWhen(false)
                setOngoing(true)
                style = Notification.MediaStyle().run {
                    setMediaSession(mSession!!.sessionToken)
                    setShowActionsInCompactView(0, 1, 2)
                }
                setLargeIcon(
                    MusicUtils.getAlbumArtFromMediaStore(
                        activeSong!!.albumId,
                        applicationContext
                    )
                )
                setSmallIcon(R.drawable.ic_music_note)
                setContentText(activeSong!!.artist)
                setContentTitle(activeSong!!.title)
                setSubText(Html.fromHtml("Playing from " + "<b>" + activeSong!!.album + "</b>", 0))
                addAction(prevSongAction)
                addAction(playPauseAction)
                addAction(nextSongAction)

            }
        } else {
            Notification.Builder(this).run {
                setShowWhen(false)
                setOngoing(true)
                style = Notification.MediaStyle().run {
                    setMediaSession(mSession!!.sessionToken)
                    setShowActionsInCompactView(0, 1, 2)
                }
                setLargeIcon(
                    MusicUtils.getAlbumArtFromMediaStore(
                        activeSong!!.albumId,
                        applicationContext
                    )
                )
                setSmallIcon(android.R.drawable.stat_sys_headset)
                setContentText(activeSong!!.artist)
                setContentTitle(activeSong!!.title)
                setSubText(activeSong!!.album)
                addAction(prevSongAction)
                addAction(playPauseAction)
                addAction(nextSongAction)
            }
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            notifId,
            notificationBuilder.build()
        )
    }

    private fun removeNotification() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notifId)
    }

    private fun playbackAction(action: Int): PendingIntent {
        val playbackAction = Intent(this, PlayerService::class.java)
        when (action) {
            0 -> {
                playbackAction.action = playAction
                return PendingIntent.getService(this, action, playbackAction, 0)
            }
            1 -> {
                playbackAction.action = pauseAction
                return PendingIntent.getService(this, action, playbackAction, 0)
            }
            2 -> {
                playbackAction.action = nextAction
                return PendingIntent.getService(this, action, playbackAction, 0)
            }
            else -> {
                playbackAction.action = prevAction
                return PendingIntent.getService(this, action, playbackAction, 0)
            }
        }
    }

    private fun handleIncomingActions(playbackAction: Intent?) {
        if (playbackAction == null || playbackAction.action == null) return
        val actionString = playbackAction.action
        when {
            actionString.equals(playAction, ignoreCase = true) -> {
                mTransportControls!!.play()
            }
            actionString.equals(pauseAction, ignoreCase = true) -> {
                mTransportControls!!.pause()
            }
            actionString.equals(nextAction, ignoreCase = true) -> {
                mTransportControls!!.skipToNext()
            }
            actionString.equals(prevAction, ignoreCase = true) -> {
                mTransportControls!!.skipToPrevious()
            }
            actionString.equals(stopAction, ignoreCase = true) -> {
                mTransportControls!!.stop()
            }
        }
    }

    private fun callStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> {
                        if (mPlayer != null) {
                            pauseMedia()
                            ongoingCall = true
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false
                                resumeMedia()
                            }
                        }
                    }
                }
            }
        }
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun initMediaPlayer() {
        mPlayer = MediaPlayer()
        mPlayer!!.setOnCompletionListener(this)
        mPlayer!!.setOnErrorListener(this)
        mPlayer!!.setOnPreparedListener(this)
        mPlayer!!.setOnBufferingUpdateListener(this)
        mPlayer!!.setOnSeekCompleteListener(this)
        mPlayer!!.setOnInfoListener(this)
        mPlayer!!.reset()
        mPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        try {
            mPlayer!!.setDataSource(activeSong!!.path)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }

        mPlayer!!.prepareAsync()
    }

    private fun initMediaSession() {
        if (mSessionManager != null) return

        mSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mSession = MediaSession(applicationContext, "PulsePlayer")
        mTransportControls = mSession!!.controller.transportControls

        mSession!!.isActive = true

        updateMetadata()

        mSession!!.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                super.onPlay()
                resumeMedia()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                skipToNext()
                updateMetadata()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                skipToPrevious()
                updateMetadata()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onStop() {
                super.onStop()
                removeNotification()
                stopSelf()
            }
        })
    }

    private fun updateMetadata() {
        mSession!!.setMetadata(MediaMetadata.Builder().run {
            putBitmap(
                MediaMetadata.METADATA_KEY_ALBUM_ART,
                MusicUtils.getAlbumArtFromMediaStore(
                    activeSong!!.albumId,
                    applicationContext
                )
            )
            putLong(MediaMetadata.METADATA_KEY_DURATION, activeSong!!.duration)
            putString(MediaMetadata.METADATA_KEY_ARTIST, activeSong!!.artist)
            putString(MediaMetadata.METADATA_KEY_ALBUM, activeSong!!.album)
            putString(MediaMetadata.METADATA_KEY_TITLE, activeSong!!.title)
            build()
        })
    }

    private fun playMedia() {
        if (!mPlayer!!.isPlaying) {
            mPlayer!!.start()
        }
    }

    private fun stopMedia() {
        if (mPlayer == null) return
        if (mPlayer!!.isPlaying) {
            mPlayer!!.stop()
        }
    }

    fun pauseMedia() {
        if (mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            resumePosition = mPlayer!!.currentPosition
        }
    }

    private fun resumeMedia() {
        if (!mPlayer!!.isPlaying) {
            mPlayer?.seekTo(resumePosition)
            mPlayer?.start()
        }
    }

    fun skipToNext() {
        if (audioIndex == audioList!!.size - 1) {
            audioIndex = 0
            activeSong = audioList!![audioIndex]
        } else {
            activeSong = audioList!![++audioIndex]
        }

        StorageUtils(applicationContext).storeAudioIndex(audioIndex)

        stopMedia()
        mPlayer!!.reset()
        initMediaPlayer()
    }


    fun skipToPrevious() {
        if (audioIndex == 0) {
            audioIndex = audioList!!.size - 1
            activeSong = audioList!![audioIndex]
        } else {
            activeSong = audioList!![--audioIndex]
        }

        StorageUtils(applicationContext).storeAudioIndex(audioIndex)

        stopMedia()
        mPlayer!!.reset()
        initMediaPlayer()
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(BecomingNoisyReceiver(), intentFilter)
    }

    private fun registerAudioReceiver() {
        val filter = IntentFilter(playNewAudioBroadcast)
        registerReceiver(newAudioReceiver, filter)
    }

    override fun onCreate() {
        super.onCreate()
        callStateListener()
        registerBecomingNoisyReceiver()
        registerAudioReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mPlayer != null) {
            stopMedia()
            mPlayer!!.release()
        }

        removeAudioFocus()

        if (phoneStateListener != null) {
            telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

        removeNotification()

        unregisterReceiver(BecomingNoisyReceiver())
        unregisterReceiver(newAudioReceiver)
        StorageUtils(applicationContext).clearCachedAudioPlaylist()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val storage = StorageUtils(applicationContext)
            audioList = storage.loadAudio()
            audioIndex = storage.loadAudioIndex()
            if (audioIndex != -1 && audioIndex < audioList!!.size) {
                activeSong = audioList!![audioIndex]
            } else {
                stopSelf()
            }
        } catch (e: NullPointerException) {
            stopSelf()
        }

        if (!requestAudioFocus()) {
            stopSelf()
        }

        if (mSessionManager == null) {
            try {
                initMediaSession()
                initMediaPlayer()

                val state: Int =
                    if (mPlayer!!.isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED

                mSession!!.setPlaybackState(
                    PlaybackState.Builder()
                        .setState(state, mPlayer!!.currentPosition.toLong(), 1.0f)
                        .build()
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }
            buildNotification(PlaybackStatus.PLAYING)
        }

        handleIncomingActions(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return iBinder
    }

    inner class PlayerBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }
}