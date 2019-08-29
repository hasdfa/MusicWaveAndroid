package com.vadim.hasdfa.musicwave.view.video

import android.graphics.Matrix

class ScaleManager(private val mViewSize: Size, private val mVideoSize: Size) {

    private val noScale: Matrix
        get() {
            val sx = mVideoSize.width / mViewSize.width.toFloat()
            val sy = mVideoSize.height / mViewSize.height.toFloat()
            return getMatrix(sx, sy, PivotPoint.LEFT_TOP)
        }

    fun getScaleMatrix(scalableType: ScalableType): Matrix? {
        when (scalableType) {
            ScalableType.NONE -> return noScale

            ScalableType.FIT_XY -> return fitXY()
            ScalableType.FIT_CENTER -> return fitCenter()
            ScalableType.FIT_START -> return fitStart()
            ScalableType.FIT_END -> return fitEnd()

            ScalableType.LEFT_TOP -> return getOriginalScale(PivotPoint.LEFT_TOP)
            ScalableType.LEFT_CENTER -> return getOriginalScale(PivotPoint.LEFT_CENTER)
            ScalableType.LEFT_BOTTOM -> return getOriginalScale(PivotPoint.LEFT_BOTTOM)
            ScalableType.CENTER_TOP -> return getOriginalScale(PivotPoint.CENTER_TOP)
            ScalableType.CENTER -> return getOriginalScale(PivotPoint.CENTER)
            ScalableType.CENTER_BOTTOM -> return getOriginalScale(PivotPoint.CENTER_BOTTOM)
            ScalableType.RIGHT_TOP -> return getOriginalScale(PivotPoint.RIGHT_TOP)
            ScalableType.RIGHT_CENTER -> return getOriginalScale(PivotPoint.RIGHT_CENTER)
            ScalableType.RIGHT_BOTTOM -> return getOriginalScale(PivotPoint.RIGHT_BOTTOM)

            ScalableType.LEFT_TOP_CROP -> return getCropScale(PivotPoint.LEFT_TOP)
            ScalableType.LEFT_CENTER_CROP -> return getCropScale(PivotPoint.LEFT_CENTER)
            ScalableType.LEFT_BOTTOM_CROP -> return getCropScale(PivotPoint.LEFT_BOTTOM)
            ScalableType.CENTER_TOP_CROP -> return getCropScale(PivotPoint.CENTER_TOP)
            ScalableType.CENTER_CROP -> return getCropScale(PivotPoint.CENTER)
            ScalableType.CENTER_BOTTOM_CROP -> return getCropScale(PivotPoint.CENTER_BOTTOM)
            ScalableType.RIGHT_TOP_CROP -> return getCropScale(PivotPoint.RIGHT_TOP)
            ScalableType.RIGHT_CENTER_CROP -> return getCropScale(PivotPoint.RIGHT_CENTER)
            ScalableType.RIGHT_BOTTOM_CROP -> return getCropScale(PivotPoint.RIGHT_BOTTOM)

            ScalableType.START_INSIDE -> return startInside()
            ScalableType.CENTER_INSIDE -> return centerInside()
            ScalableType.END_INSIDE -> return endInside()

            else -> return null
        }
    }

    private fun getMatrix(sx: Float, sy: Float, px: Float, py: Float): Matrix {
        val matrix = Matrix()
        matrix.setScale(sx, sy, px, py)
        return matrix
    }

    private fun getMatrix(sx: Float, sy: Float, pivotPoint: PivotPoint): Matrix {
        when (pivotPoint) {
            PivotPoint.LEFT_TOP -> return getMatrix(sx, sy, 0f, 0f)
            PivotPoint.LEFT_CENTER -> return getMatrix(sx, sy, 0f, mViewSize.height / 2f)
            PivotPoint.LEFT_BOTTOM -> return getMatrix(sx, sy, 0f, mViewSize.height.toFloat())
            PivotPoint.CENTER_TOP -> return getMatrix(sx, sy, mViewSize.width / 2f, 0f)
            PivotPoint.CENTER -> return getMatrix(sx, sy, mViewSize.width / 2f, mViewSize.height / 2f)
            PivotPoint.CENTER_BOTTOM -> return getMatrix(sx, sy, mViewSize.width / 2f, mViewSize.height.toFloat())
            PivotPoint.RIGHT_TOP -> return getMatrix(sx, sy, mViewSize.width.toFloat(), 0f)
            PivotPoint.RIGHT_CENTER -> return getMatrix(sx, sy, mViewSize.width.toFloat(), mViewSize.height / 2f)
            PivotPoint.RIGHT_BOTTOM -> return getMatrix(sx, sy, mViewSize.width.toFloat(), mViewSize.height.toFloat())
            else -> throw IllegalArgumentException("Illegal PivotPoint")
        }
    }

    private fun getFitScale(pivotPoint: PivotPoint): Matrix {
        var sx = mViewSize.width.toFloat() / mVideoSize.width
        var sy = mViewSize.height.toFloat() / mVideoSize.height
        val minScale = Math.min(sx, sy)
        sx = minScale / sx
        sy = minScale / sy
        return getMatrix(sx, sy, pivotPoint)
    }

    private fun fitXY(): Matrix {
        return getMatrix(1f, 1f, PivotPoint.LEFT_TOP)
    }

    private fun fitStart(): Matrix {
        return getFitScale(PivotPoint.LEFT_TOP)
    }

    private fun fitCenter(): Matrix {
        return getFitScale(PivotPoint.CENTER)
    }

    private fun fitEnd(): Matrix {
        return getFitScale(PivotPoint.RIGHT_BOTTOM)
    }

    private fun getOriginalScale(pivotPoint: PivotPoint): Matrix {
        val sx = mVideoSize.width / mViewSize.width.toFloat()
        val sy = mVideoSize.height / mViewSize.height.toFloat()
        return getMatrix(sx, sy, pivotPoint)
    }

    private fun getCropScale(pivotPoint: PivotPoint): Matrix {
        var sx = mViewSize.width.toFloat() / mVideoSize.width
        var sy = mViewSize.height.toFloat() / mVideoSize.height
        val maxScale = Math.max(sx, sy)
        sx = maxScale / sx
        sy = maxScale / sy
        return getMatrix(sx, sy, pivotPoint)
    }

    private fun startInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            // video is smaller than view size
            getOriginalScale(PivotPoint.LEFT_TOP)
        } else {
            // either of width or height of the video is larger than view size
            fitStart()
        }
    }

    private fun centerInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            // video is smaller than view size
            getOriginalScale(PivotPoint.CENTER)
        } else {
            // either of width or height of the video is larger than view size
            fitCenter()
        }
    }

    private fun endInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            // video is smaller than view size
            getOriginalScale(PivotPoint.RIGHT_BOTTOM)
        } else {
            // either of width or height of the video is larger than view size
            fitEnd()
        }
    }
}
