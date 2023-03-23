package com.wdp.saver

import android.content.Context
import android.text.format.DateFormat
import java.io.File
import java.util.*

fun getDefaultSaveFile(context: Context, fileExtName: String): String {
    val dir = context.externalCacheDir?.path
    val name = "${DateFormat.format("yyyy-MM-dd-HH-mm-ss", Date())}.$fileExtName"
    return dir + File.separator + name
}