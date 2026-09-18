package com.example.geumtophotocleaner

enum class MediaAccess { FULL, PARTIAL, DENIED }

object PhotoAccess {
    fun resolve(fullGranted: Boolean, partialGranted: Boolean): MediaAccess = when {
        fullGranted -> MediaAccess.FULL
        partialGranted -> MediaAccess.PARTIAL
        else -> MediaAccess.DENIED
    }
}
