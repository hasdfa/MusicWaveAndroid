package com.vadim.hasdfa.musicwave.base

import android.app.Activity
import android.content.Context

abstract class IActivityController {

    protected val activity: Activity
    protected val context: Context
        get() = activity

    constructor(activity: Activity) {
        this.activity = activity
    }



}