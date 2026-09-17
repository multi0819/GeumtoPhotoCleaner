package com.example.geumtophotocleaner

import android.net.Uri

data class PhotoItem(val uri: Uri, val takenMillis: Long, var selected: Boolean = false)
