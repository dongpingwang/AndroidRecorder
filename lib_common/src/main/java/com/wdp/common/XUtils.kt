package com.wdp.common

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
object XUtils {

    private var sApplication by Delegates.notNull<Application>()

    fun init(application: Application) {
        sApplication = application
    }


    fun getApplication(): Application = sApplication

    fun getContext(): Context = sApplication
}