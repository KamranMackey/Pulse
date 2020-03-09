package com.kamranmackey.pulse.backend.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kamranmackey.pulse.backend.models.PlaybackStatus
import com.kamranmackey.pulse.backend.services.PlayerService

class BecomingNoisyReceiver: BroadcastReceiver() {

    private val mPlayerService: PlayerService = PlayerService()

    override fun onReceive(context: Context?, intent: Intent?) {
        mPlayerService.pauseMedia()
        mPlayerService.buildNotification(PlaybackStatus.PAUSED)
    }

}