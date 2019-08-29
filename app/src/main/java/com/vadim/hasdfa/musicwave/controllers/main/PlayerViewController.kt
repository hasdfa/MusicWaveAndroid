package com.vadim.hasdfa.musicwave.controllers.main

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.gauravk.audiovisualizer.base.BaseVisualizer
import com.vadim.hasdfa.musicwave.R
import com.vadim.hasdfa.musicwave.base.IViewController
import com.vadim.hasdfa.musicwave.models.Artist
import com.vadim.hasdfa.musicwave.view.video.CenterCropVideoView

class PlayerViewController: IViewController {

    constructor(view: View): super(view)
    constructor(context: Context, @LayoutRes res: Int): super(context, res)

    private val videoPath by lazy {
        Uri.parse(
            "android.resource://" + context.packageName + "/" + R.raw.music
        )
    }

    private val mVideoView by lazy { view.findViewById<CenterCropVideoView>(R.id.mCoverVideoView) }
    private val mVisualizer by lazy { view.findViewById<BaseVisualizer>(R.id.visualizerView) }
    private val mTitle by lazy { view.findViewById<TextView>(R.id.titleTextView) }

    init {
        mVideoView.apply {
            mVideoView.setRawData(R.raw.music)
            prepare(MediaPlayer.OnPreparedListener {
                it.isLooping = true

                if (it.audioSessionId != -1)
                    mVisualizer.setAudioSessionId(audioSessionId)
            })
        }
    }

    val onChose: (Artist) -> Unit = {
        mVideoView.mMediaPlayer?.seekTo(0)
        mTitle.text = it.name
    }

    fun play() {
        mVideoView.start()
    }

    fun pause() {
        mVideoView.pause()
    }

}