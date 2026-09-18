package com.example.geumtophotocleaner

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import java.time.ZoneId

class PhotoScanner(private val context: Context) {
    fun scan(onProgress: (Int) -> Unit = {}): List<PhotoItem> {
        val result = mutableListOf<PhotoItem>()
        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val cutoff = PhotoRules.startOfTodayMillis(System.currentTimeMillis(), ZoneId.systemDefault())
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN)
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} > 0 AND ${MediaStore.Images.Media.DATE_TAKEN} < ?"
        var count = 0
        resolver.query(collection, projection, selection, arrayOf(cutoff.toString()), "${MediaStore.Images.Media.DATE_TAKEN} DESC")?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            while (c.moveToNext()) {
                count++; if (count % 10 == 0) onProgress(count)
                val uri = ContentUris.withAppendedId(collection, c.getLong(idCol))
                val taken = c.getLong(dateCol)
                val latLong = try {
                    resolver.openFileDescriptor(uri, "r")?.use { pfd -> ExifInterface(pfd.fileDescriptor).latLong }
                } catch (_: Exception) { null }
                if (latLong == null) continue
                val lat = latLong[0]; val lon = latLong[1]
                if (PhotoRules.isWithinGeumtoArea(lat, lon)) result += PhotoItem(uri, taken)
            }
        }
        return result
    }
}
