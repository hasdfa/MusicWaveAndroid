package com.vadim.hasdfa.musicwave.activities

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vadim.hasdfa.musicwave.R
import com.vadim.hasdfa.musicwave.controllers.main.ArtistViewController
import com.vadim.hasdfa.musicwave.controllers.main.PermissionActivityController
import com.vadim.hasdfa.musicwave.controllers.main.PlayerViewController
import com.vadim.hasdfa.musicwave.models.Artist
import com.vadim.hasdfa.musicwave.view.wavebar.SpringInterpolator
import com.vadim.hasdfa.musicwave.view.wavebar.WaveSideBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity: AppCompatActivity(), WaveSideBar.OnStateChangeListener {

    private val xCoef: Float = 0.15f
    private val yCoef: Float = 0.7f

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private val permissionController: PermissionActivityController by lazy {
        PermissionActivityController(
            this, permissions
        )
    }

    private val artistsViewController: ArtistViewController by lazy {
        ArtistViewController(this, R.layout.fragment_artists)
    }
    private val playerViewController: PlayerViewController by lazy {
        PlayerViewController(this, R.layout.fragment_player)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (permissionController.isPermissionGranted)
            initialize()
        else
            permissionController.requestPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && permissionController.isPermissionGranted)
            initialize()
        else
            exitProcess(1)
    }

    private fun initialize() {
        setupDefaultSideBar(mLeftSideBar)
        setupLeftSideBar()

        setupDefaultSideBar(mRightSideBar)
        setupRightSideBar()

        mPlayButton.x = mParent.width * xCoef - mPlayButton.width / 2
        mPlayButton.y = mParent.height * yCoef - mPlayButton.height / 2
        mPlayButton.setOnClickListener {
            if (mPlayButton.isFirst)
                playerViewController.pause()
            else
                playerViewController.play()
        }

        artistsViewController.setOnChoseListener(onChose)
        artistsViewController.setOnLoadCompleteListener {
            mLoadingPane.visibility = View.INVISIBLE
        }

        mRightSideBar.expandAnimationDuration = 0
        mLeftSideBar.expandAnimationDuration  = 0

        mRightSideBar.expand()
        mRightSideBar.collapse()
        mLeftSideBar.expand()

        GlobalScope.launch {
            delay(1000)
            MainScope().launch {
                mLeftSideBar.expand()
                mLeftSideBar.expandAnimationDuration  = 800L
                mRightSideBar.expandAnimationDuration = 800L
            }
        }
    }

    private val onChose: (Artist) -> Unit = {
        playerViewController.onChose(it)

        mRightSideBar.expand().apply {
            willExpand(mRightSideBar)
        }
    }

    private fun setupDefaultSideBar(bar: WaveSideBar) {
        bar.onStateChangeListener = this
        bar.minXCoef = xCoef
        bar.minYCoef = yCoef

        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        bar.barWidthPixels = metrics.widthPixels
    }

    private fun setupLeftSideBar() {
        mLeftSideBar.startColorRes = android.R.color.white
        mLeftSideBar.endColorRes = android.R.color.white

        mLeftSideBar.view = artistsViewController.view
    }

    private fun setupRightSideBar() {
        mRightSideBar.minYCoef = 1f - yCoef
        mRightSideBar.orientation = WaveSideBar.Orientation.Right

        mRightSideBar.startColorRes = android.R.color.black
        mRightSideBar.endColorRes = android.R.color.black

        mRightSideBar.view = playerViewController.view
    }

    private fun WaveSideBar.other(): WaveSideBar
        = if (this == mLeftSideBar) mRightSideBar else mLeftSideBar

    private fun processX(sender: WaveSideBar, x: Float) =
        if (sender.orientation == WaveSideBar.Orientation.Left)
            x - mPlayButton.width * 1.1f else sender.width - x

    private fun processY(sender: WaveSideBar, y: Float) =
        (if (sender.orientation == WaveSideBar.Orientation.Left)
            y else sender.height - y) - mPlayButton.height / 2

    override fun didMove(sender: WaveSideBar, x: Float, y: Float) {
        if (sender.isExpanded || sender.isBusy)
            return

        mPlayButton.animate()
            .translationX(processX(sender, x))
            .translationY(processY(sender, y))
            .apply { duration =  0 }
            .start()
    }

    override fun willExpand(sender: WaveSideBar) {
        val x: Float
        val y: Float

        val other = sender.other()
        other.apply {
            x = processX(this, minX)
            y = processY(this, minY)
        }

        val animationX = ObjectAnimator.ofFloat(mPlayButton, "x", x)
        val animationY = ObjectAnimator.ofFloat(mPlayButton, "y", y)
        AnimatorSet().apply {
            play(animationX)
                .with(animationY)
            duration = mRightSideBar.expandAnimationDuration * 2
            interpolator = SpringInterpolator()
        }.start()
    }

    override fun didExpanded(sender: WaveSideBar) {
        val other = sender.other()

        other.z = 64f
        sender.z = 32f
        other.collapse()

        didMove(
            other,
            other.currentX,
            other.currentY
        )

        if (sender == mLeftSideBar) {
            mParent.setBackgroundColor(Color.BLACK)
        } else {
            mParent.setBackgroundColor(Color.WHITE)
        }
    }
}