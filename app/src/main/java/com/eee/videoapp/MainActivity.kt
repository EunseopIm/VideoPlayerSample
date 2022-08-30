package com.eee.videoapp

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerReadyListener
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerStateListener
import com.ct7ct7ct7.androidvimeoplayer.model.TextTrack
import com.eee.videoapp.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import java.util.regex.*

class MainActivity : AppCompatActivity() {

    // Single Exoplayer
    private var autoPlay = false
    private val currentWindow = 0
    private val playbackPosition = 0L
    private var exoPlayer: ExoPlayer? = null

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Youtube
        initYoutubePlayer()

        // ExoPlayer
        initExoPlayer()

        // Vimeo
        initVimeoPlayer()
    }

    override fun onResume() {
        super.onResume()

        if (autoPlay) {
            exoPlayer?.let { it.playWhenReady = true }
        }
    }

    override fun onPause() {
        super.onPause()

        if (isPlaying()) {

            exoPlayer?.let { it.playWhenReady = false }
            autoPlay = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 종료시 release
        releasePlayer()
    }

    /**
     * Youtube ID 추출
     */
    private fun getYouTubeId(youTubeUrl: String): String? {

        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "error"
        }
    }

    /**
     * YoutubePlayer 초기화
     */
    private fun initYoutubePlayer() {

        val youTubeUrl = "https://www.youtube.com/watch?v=SzaGCG9PiqA"

        // Youtube
        lifecycle.addObserver(binding.youtubeView)
        val youtubeId = getYouTubeId(youTubeUrl)?: ""
        binding.youtubeView.enableAutomaticInitialization = false
        binding.youtubeView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.loadVideo(youtubeId, 0f)
            }
        }, true)

        binding.youtubeView.addYouTubePlayerListener(object: YouTubePlayerListener {

            override fun onApiChange(youTubePlayer: YouTubePlayer) {}
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {

                //Log.v(">>>" , "YoutubePlayer - second($second)")
            }
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}
            override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}
            override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}
            override fun onReady(youTubePlayer: YouTubePlayer) {}
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}
            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}
            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {

                if (state == PlayerConstants.PlayerState.ENDED) {

                    Toast.makeText(this@MainActivity, "YoutubePlayer - PlayerState.ENDED", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * ExoPlayer 초기화
     */
    private fun initExoPlayer() {

        binding.exoView.useController = true

        exoPlayer = ExoPlayer.Builder(this).build()

        exoPlayer?.let {

            binding.exoView.player = it

            it.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    // 영상 종료 시
                    if (playbackState == Player.STATE_ENDED) {

                        Toast.makeText(this@MainActivity, "ExoPlayer - Player.STATE_ENDED", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if (isPlaying) {
                        // Active playback.
                    } else {
                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.getPlayWhenReady,
                        // player.getPlaybackState, player.getPlaybackSuppressionReason and
                        // player.getPlaybackError for details.
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                }
            })

            // url 정보 세팅
            //val targetURL = "http://playertest.longtailvideo.com/adaptive/wowzaid3/playlist.m3u8"
            val targetURL = "https://youtu.be/OCiUWfFz9Mc"
            val uri = Uri.parse(targetURL)
            val mediaItem = MediaItem.fromUri(uri)
            it.setMediaItem(mediaItem)

            it.prepare()
            it.play()
        }
    }

    /**
     * ExoPlayer - releasePlayer
     */
    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    /**
     * ExoPlayer - isPlaying
     */
    private fun isPlaying(): Boolean {

        var result = false
        exoPlayer?.let {
            result = it.playbackState == Player.STATE_READY && it.playWhenReady
        }

        return result
    }

    /**
     * VimeoPlayer 초기화
     */
    private fun initVimeoPlayer() {

        lifecycle.addObserver(binding.vimeoPlayerView)
        binding.vimeoPlayerView.initialize(true, 59777392)

        // video background settings is OPEN and limit playing at embedded.
        //binding.vimeoPlayerView.initialize(true, {YourPrivateVideoId}, "SettingsEmbeddedUrl")

        //video background settings is PRIVATE.
        //binding.vimeoPlayerView.initialize(true, {YourPrivateVideoId},"VideoHashKey", "SettingsEmbeddedUrl")

        binding.vimeoPlayerView.addTimeListener { second ->
            Log.v(">>>", "Vimeo Time Listener : $second")
        }

        binding.vimeoPlayerView.addErrorListener { message, method, name ->
            Log.v(">>>", "Vimeo Error : $message / $method / $name")
        }

        binding.vimeoPlayerView.addReadyListener(object : VimeoPlayerReadyListener {
            override fun onReady(
                title: String?,
                duration: Float,
                textTrackArray: Array<TextTrack>
            ) {
                Log.v(">>>", "Vimeo Ready Listener : onReady")
            }

            override fun onInitFailed() {
                Log.v(">>>", "Vimeo Ready Listener : onInitFailed")
            }
        })

        binding.vimeoPlayerView.addStateListener(object : VimeoPlayerStateListener {
            override fun onPlaying(duration: Float) {

                Log.v(">>>", "Vimeo State Listener : onPlaying")
            }

            override fun onPaused(seconds: Float) {

                Log.v(">>>", "Vimeo State Listener : onPaused")
            }

            override fun onEnded(duration: Float) {

                Log.v(">>>", "Vimeo State Listener : onEnded")
            }
        })

        /*binding.volumeSeekBar.progress = 100
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var volume = progress.toFloat() / 100
                binding.vimeoPlayerView.setVolume(volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.vimeoPlayerView.addVolumeListener { volume ->
            binding.playerVolumeTextView.text = getString(R.string.player_volume, volume.toString())
        }

        binding.playButton.setOnClickListener {
            binding.vimeoPlayerView.play()
        }

        binding.pauseButton.setOnClickListener {
            binding.vimeoPlayerView.pause()
        }

        binding.getCurrentTimeButton.setOnClickListener {
            Toast.makeText(
                this,
                getString(
                    R.string.player_current_time,
                    binding.vimeoPlayerView.currentTimeSeconds.toString()
                ),
                Toast.LENGTH_LONG
            ).show()
        }

        binding.loadVideoButton.setOnClickListener {
            binding.vimeoPlayerView.loadVideo(19231868)
        }

        binding.colorButton.setOnClickListener {
            binding.vimeoPlayerView.topicColor = Color.GREEN
        }*/
    }

}