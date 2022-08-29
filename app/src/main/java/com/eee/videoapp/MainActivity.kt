package com.eee.videoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eee.videoapp.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import java.util.regex.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 유튜브 플레이어
        initYoutubePlayer()
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
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}
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
}