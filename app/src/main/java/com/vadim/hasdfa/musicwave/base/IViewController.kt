package com.vadim.hasdfa.musicwave.base

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class IViewController {

    var view: View

    protected val context: Context
        get() = view.context

    protected val activity: Activity?
        get() = context as? Activity

    protected val appCompatActivity: AppCompatActivity?
        get() = context as? AppCompatActivity

    constructor(view: View) {
        this.view = view
    }

    constructor(context: Context, @LayoutRes res: Int): this(
        LayoutInflater.from(context).inflate(
            res, null, false
        )
    )

}