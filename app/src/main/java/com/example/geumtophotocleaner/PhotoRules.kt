package com.example.geumtophotocleaner

import java.time.Instant
import java.time.ZoneId

object PhotoRules {
    fun startOfTodayMillis(nowMillis: Long, zoneId: ZoneId): Long =
        Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun isBeforeToday(takenMillis: Long, nowMillis: Long, zoneId: ZoneId): Boolean =
        takenMillis in 1 until startOfTodayMillis(nowMillis, zoneId)

    fun isGeumtoAddress(parts: List<String?>): Boolean {
        val text = parts.filterNotNull().joinToString(" ")
        val geumto = text.contains("금토동") || text.contains("Geumto-dong", ignoreCase = true)
        val seongnam = text.contains("성남") || text.contains("Seongnam", ignoreCase = true)
        val sujeong = text.contains("수정구") || text.contains("Sujeong", ignoreCase = true)
        return geumto && seongnam && sujeong
    }
}
