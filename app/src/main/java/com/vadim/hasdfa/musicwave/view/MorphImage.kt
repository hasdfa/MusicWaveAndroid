package com.vadim.hasdfa.musicwave.view

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.vadim.hasdfa.musicwave.R

class MorphImage: AppCompatImageView {

    private var first: Drawable? = null
    private var second: Drawable? = null

    var isFirst: Boolean = true

    public var firstDrawable: Drawable?
        get() = first
        set(value) {
            first = value
        }

    public var secondDrawable: Drawable?
        get() = second
        set(value) {
            second = value
        }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.MorphImage,
            0, 0).apply {

            try {
                val id1 = getResourceId(R.styleable.MorphImage_firstDrawable, android.R.drawable.ic_delete)
                val id2 = getResourceId(R.styleable.MorphImage_secondDrawable, android.R.drawable.ic_delete)

                firstDrawable = ContextCompat.getDrawable(context, id1)
                secondDrawable =  ContextCompat.getDrawable(context, id2)
            } finally {
                recycle()
            }

            setImageDrawable(first)
        }
    }

    public fun morph() {
        setImageDrawable(if(isFirst) second else first)
        (drawable as? AnimatedImageDrawable)?.start()

        isFirst = !isFirst
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener {
            morph(); l?.onClick(it)
        }
    }

}