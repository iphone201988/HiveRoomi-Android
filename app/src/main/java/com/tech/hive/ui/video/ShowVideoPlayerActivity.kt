package com.tech.hive.ui.video

import android.util.Log
import androidx.activity.viewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.ActivityShowVideoPlayerBinding
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowVideoPlayerActivity : BaseActivity<ActivityShowVideoPlayerBinding>() {

    private val viewModel: ShowVideoPlayerActivityVM by viewModels()
    var videoPlayer: String? = null
    private var player: ExoPlayer? = null
    var stopProgress = 0
    override fun getLayoutResource(): Int {
        return R.layout.activity_show_video_player
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ShowVideoPlayerActivity)
        // intent get data
        videoPlayer = intent.getStringExtra("videoUrl")
        // initialize Exo player
        initializePlayer()

        // click
        iniOnCLick()
    }

    /** all click handel function **/
    private fun iniOnCLick() {
        viewModel.onClick.observe(this@ShowVideoPlayerActivity) {
            when (it?.id) {
//                R.id.ivBack -> {
//                    onBackPressedDispatcher.onBackPressed()
//                }
            }
        }
    }

    /**** video play function ***/
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this) // <- context
            .build()
        // create a media item.
        val mediaItem = MediaItem.Builder().setUri(Constants.BASE_URL_IMAGE + videoPlayer)
            .setMimeType(MimeTypes.APPLICATION_MP4).build()
        // Create a media source and pass the media item
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this) // <- context
        ).createMediaSource(mediaItem)
        // Finally assign this media source to the player
        player!!.apply {
            setMediaSource(mediaSource)
            playWhenReady = true // start playing when the exoplayer has setup
            seekTo(0, 0L) // Start from the beginning
            prepare()
            play()// Change the state from idle.
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)/*       if (isPlaying) {
                               hideLoading()
                           } else {
                               if (stopProgress == 1) {
                                   hideLoading()
                                   stopProgress = 0
                               } else {
                                   showLoading()
                               }
                           }*/
                    hideLoading()
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    if (playWhenReady) {
                        // Video is playing, hide the loading indicator
                        hideLoading()
                    } else {
                        stopProgress = 1
                        // Video is paused, show the loading indicator
                        hideLoading()
                    }
                }
            })
        }.also {
            // Do not forget to attach the player to the view
            binding.playerView.player = it
        }

    }

    /**** on stop method call ***/
    override fun onStop() {
        super.onStop()
        player!!.release()
        player = null
    }
}