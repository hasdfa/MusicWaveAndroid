package com.vadim.hasdfa.musicwave.view.video

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RawRes
import com.vadim.hasdfa.musicwave.R
import java.io.FileDescriptor
import java.io.IOException

class CenterCropVideoView: TextureView, TextureView.SurfaceTextureListener,
    MediaPlayer.OnVideoSizeChangedListener {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        if (attrs == null) {
            return
        }

        val arr = context.run {
            obtainStyledAttributes(attrs, R.styleable.CenterCropVideoView, 0, 0)
        } ?: return

        val scaleType = arr.getInt(R.styleable.CenterCropVideoView_scalableType, ScalableType.NONE.ordinal)
        mScalableType = ScalableType.values()[scaleType]
    }

    val audioSessionId: Int
        get() = mMediaPlayer?.audioSessionId ?: -1

    init {
        mScalableType = ScalableType.CENTER_CROP
    }

    var mMediaPlayer: MediaPlayer? = null
    var mScalableType = ScalableType.NONE


    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        val surface = Surface(surfaceTexture)
        if (mMediaPlayer != null) {
            mMediaPlayer?.setSurface(surface)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isPlaying()) {
            stop()
        }
        release()
    }

    override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {
        scaleVideoSize(width, height)
    }

    private fun scaleVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth == 0 || videoHeight == 0) {
            return
        }

        val viewSize = Size(width, height)
        val videoSize = Size(videoWidth, videoHeight)
        val scaleManager = ScaleManager(viewSize, videoSize)
        val matrix = scaleManager.getScaleMatrix(mScalableType)
        if (matrix != null) {
            setTransform(matrix)
        }
    }

    private fun initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setOnVideoSizeChangedListener(this)
            surfaceTextureListener = this
        } else {
            reset()
        }
    }

    @Throws(IOException::class)
    fun setRawData(@RawRes id: Int) {
        val afd = resources.openRawResourceFd(id)
        setDataSource(afd)
    }

    @Throws(IOException::class)
    fun setAssetData(assetName: String) {
        val manager = context.assets
        val afd = manager.openFd(assetName)
        setDataSource(afd)
    }

    @Throws(IOException::class)
    private fun setDataSource(afd: AssetFileDescriptor) {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
    }

    @Throws(IOException::class)
    fun setDataSource(path: String) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(path)
    }

    @Throws(IOException::class)
    fun setDataSource(
        context: Context, uri: Uri,
        headers: Map<String, String>?
    ) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(context, uri, headers)
    }

    @Throws(IOException::class)
    fun setDataSource(context: Context, uri: Uri) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(context, uri)
    }

    @Throws(IOException::class)
    fun setDataSource(fd: FileDescriptor, offset: Long, length: Long) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(fd, offset, length)
    }

    @Throws(IOException::class)
    fun setDataSource(fd: FileDescriptor) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(fd)
    }

    fun setScalableType(scalableType: ScalableType) {
        mScalableType = scalableType
        scaleVideoSize(getVideoWidth(), getVideoHeight())
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun prepare(listener: MediaPlayer.OnPreparedListener?) {
        mMediaPlayer?.setOnPreparedListener(listener)
        mMediaPlayer?.prepare()
    }

    @Throws(IllegalStateException::class)
    fun prepareAsync(listener: MediaPlayer.OnPreparedListener?) {
        mMediaPlayer?.setOnPreparedListener(listener)
        mMediaPlayer?.prepareAsync()
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun prepare() {
        prepare(null)
    }

    @Throws(IllegalStateException::class)
    fun prepareAsync() {
        prepareAsync(null)
    }

    fun setOnErrorListener(listener: MediaPlayer.OnErrorListener?) {
        mMediaPlayer?.setOnErrorListener(listener)
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener?) {
        mMediaPlayer?.setOnCompletionListener(listener)
    }

    fun setOnInfoListener(listener: MediaPlayer.OnInfoListener?) {
        mMediaPlayer?.setOnInfoListener(listener)
    }

    fun getCurrentPosition(): Int {
        return mMediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mMediaPlayer?.duration ?: 0
    }

    fun getVideoHeight(): Int {
        return mMediaPlayer?.videoHeight ?: 0
    }

    fun getVideoWidth(): Int {
        return mMediaPlayer?.videoWidth ?: 0
    }

    fun isLooping(): Boolean {
        return mMediaPlayer?.isLooping ?: false
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }

    fun pause() {
        mMediaPlayer?.pause()
    }

    fun seekTo(msec: Int) {
        mMediaPlayer?.seekTo(msec)
    }

    fun setLooping(looping: Boolean) {
        mMediaPlayer?.isLooping = looping
    }

    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mMediaPlayer?.setVolume(leftVolume, rightVolume)
    }

    fun start() {
        mMediaPlayer?.start()
    }

    fun stop() {
        mMediaPlayer?.stop()
    }

    fun reset() {
        mMediaPlayer?.reset()
    }

    fun release() {
        reset()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

}