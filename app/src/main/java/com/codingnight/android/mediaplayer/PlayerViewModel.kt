package com.codingnight.android.mediaplayer

import android.app.Application
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val mediaPlayer = MyMediaPlayer()

    private val _progressBarVisibility = MutableLiveData(View.VISIBLE)
    val progressBarVisibility : LiveData<Int> = _progressBarVisibility

    private val _videoResolution = MutableLiveData(Pair(0, 0))
    val videoResolution : LiveData<Pair<Int, Int>> = _videoResolution

    init {
        loadVideo()
    }

    fun loadVideo() {
        mediaPlayer.apply {
            reset()
            _progressBarVisibility.value = View.VISIBLE
            setDataSource("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218093206z8V1JuPlpe.mp4")
            setOnPreparedListener {
                _progressBarVisibility.value = View.INVISIBLE
                isLooping = true
                it.start()
            }
            setOnVideoSizeChangedListener { mp, width, height ->
                _videoResolution.value = Pair(width, height)
            }
            prepareAsync()
        }
    }

    fun emitVideoResolution() {
        _videoResolution.value = _videoResolution.value
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}