package com.codingnight.android.mediaplayer

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val mediaPlayer = MyMediaPlayer()

    private val _bufferPercent = MutableLiveData(0)
    val bufferPercent: LiveData<Int> = _bufferPercent

    private val _controllerFrameVisibility = MutableLiveData(View.INVISIBLE)
    val controllerFrameVisibility: LiveData<Int> = _controllerFrameVisibility

    private var controllerShowTime = 0L

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
            setOnVideoSizeChangedListener { _, width, height ->
                _videoResolution.value = Pair(width, height)
            }
            setOnBufferingUpdateListener { _, percent ->
                _bufferPercent.value = percent
            }
            setOnSeekCompleteListener {
                mediaPlayer.start()
                _progressBarVisibility.value = View.INVISIBLE
            }
            prepareAsync()
        }
    }

    fun toggleControllerVisibility() {
        if (_controllerFrameVisibility.value == View.INVISIBLE) {
            _controllerFrameVisibility.value = View.VISIBLE
            controllerShowTime = System.currentTimeMillis()
            viewModelScope.launch {
                delay(3000)
                if (System.currentTimeMillis() - controllerShowTime > 3000)
                    _controllerFrameVisibility.value = View.INVISIBLE
            }
        } else {
            _controllerFrameVisibility.value = View.INVISIBLE
        }
    }

    fun playerSeekToProgress(progress: Int) {
        _progressBarVisibility.value = View.VISIBLE
        mediaPlayer.seekTo(progress)
    }

    fun emitVideoResolution() {
        _videoResolution.value = _videoResolution.value
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}