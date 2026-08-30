package com.example.audio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.IslamicApp
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuranMediaService : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioPlayerHelper: AudioPlayerHelper
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    companion object {
        const val CHANNEL_ID = "quran_media_channel"
        const val NOTIFICATION_ID = 404
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_STOP = "action_stop"
    }

    override fun onCreate() {
        super.onCreate()
        
        audioPlayerHelper = (application as IslamicApp).audioPlayerHelper
        
        createNotificationChannel()
        
        mediaSession = MediaSessionCompat(this, "QuranMediaSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    audioPlayerHelper.resume()
                }
                override fun onPause() {
                    audioPlayerHelper.pause()
                }
                override fun onSkipToNext() {
                    // Could implement if we had next in AudioPlayerHelper
                }
                override fun onSkipToPrevious() {
                    // Could implement if we had prev in AudioPlayerHelper
                }
                override fun onStop() {
                    audioPlayerHelper.stop()
                    stopSelf()
                }
            })
            isActive = true
        }

        serviceScope.launch {
            audioPlayerHelper.playerState.collectLatest { state ->
                updateMediaSessionState(state)
                if (state.isPlaying || state.isBuffering || state.currentSurahNumber != null) {
                    updateNotification(state)
                } else if (!state.isPlaying) {
                    stopForeground(false)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> audioPlayerHelper.resume()
            ACTION_PAUSE -> audioPlayerHelper.pause()
            ACTION_STOP -> {
                audioPlayerHelper.stop()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun updateMediaSessionState(state: QuranAudioPlayerState) {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .setState(
                if (state.isPlaying) PlaybackStateCompat.STATE_PLAYING
                else if (state.isBuffering) PlaybackStateCompat.STATE_BUFFERING
                else PlaybackStateCompat.STATE_PAUSED,
                state.currentPositionMs,
                1f
            )
        mediaSession.setPlaybackState(stateBuilder.build())

        val metadataBuilder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, state.currentSurahEnglishName ?: "Quran")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, state.reciter.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, state.currentSurahArabicName)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, state.durationMs)

        mediaSession.setMetadata(metadataBuilder.build())
    }

    private fun updateNotification(state: QuranAudioPlayerState) {
        val playPauseAction = if (state.isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                PendingIntent.getService(this, 1, Intent(this, QuranMediaService::class.java).setAction(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                PendingIntent.getService(this, 2, Intent(this, QuranMediaService::class.java).setAction(ACTION_PLAY), PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel, "Stop",
            PendingIntent.getService(this, 3, Intent(this, QuranMediaService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_IMMUTABLE)
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(state.currentSurahEnglishName ?: "Quran")
            .setContentText(state.reciter.name)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(MediaStyle()
                .setShowActionsInCompactView(0, 1)
                .setMediaSession(mediaSession.sessionToken)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(state.isPlaying)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows media controls for Quran recitation"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.isActive = false
        mediaSession.release()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
