package com.codingnight.android.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var surfaceView: SurfaceView
    private lateinit var playerFrame: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        playerFrame = findViewById<FrameLayout>(R.id.playerFrame)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java).apply {
            progressBarVisibility.observe(this@MainActivity) {
                progressBar.visibility = it
            }
            // TODO resize surfaceView
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
}