package com.codingnight.android.mediaplayer

import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var surfaceView: SurfaceView
    private lateinit var playerFrame: FrameLayout
    private lateinit var controllerFrame: FrameLayout
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView = findViewById(R.id.surfaceView)
        playerFrame = findViewById(R.id.playerFrame)
        progressBar = findViewById(R.id.progressBar)
        controllerFrame = findViewById(R.id.controller_frame)
        seekBar = findViewById(R.id.seekBar)

        updatePlayerProgress()

        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java).apply {
            progressBarVisibility.observe(this@MainActivity) {
                progressBar.visibility = it
            }
            videoResolution.observe(this@MainActivity) {
                seekBar.max = mediaPlayer.duration
                playerFrame.post { resizePlayer(it.first, it.second) }
            }
            controllerFrameVisibility.observe(this@MainActivity, Observer {
                controllerFrame.visibility = it
            })
            bufferPercent.observe(this@MainActivity, Observer {
                seekBar.secondaryProgress = seekBar.max * it / 100
            })
        }

        lifecycle.addObserver(playerViewModel.mediaPlayer)

        playerFrame.setOnClickListener {
            playerViewModel.toggleControllerVisibility()
        }
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                playerViewModel.mediaPlayer.setDisplay(holder)
                playerViewModel.mediaPlayer.setScreenOnWhilePlaying(true)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }

    private fun updatePlayerProgress() {
        lifecycleScope.launch {
            while (true) {
                delay(500)
                seekBar.progress = playerViewModel.mediaPlayer.currentPosition
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (resources.configuration.orientation ==  Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI()
            playerViewModel.emitVideoResolution()
        }
    }

    private fun resizePlayer(width: Int, height: Int) {
        if (width == 0 || height == 0)
            return
        surfaceView.layoutParams = FrameLayout.LayoutParams(
            playerFrame.height * width / height,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}