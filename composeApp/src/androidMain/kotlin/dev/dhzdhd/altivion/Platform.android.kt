package dev.dhzdhd.altivion

import android.os.Build
import dev.dhzdhd.altivion.Platform

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()