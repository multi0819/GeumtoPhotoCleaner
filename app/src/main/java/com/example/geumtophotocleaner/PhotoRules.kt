package com.example.geumtophotocleaner

import java.time.Instant
import java.time.ZoneId

object PhotoRules {
    // 금토동 중심 좌표를 둘러싼 검색 영역. 주소 문자열이 '수정구'까지만
    // 제공되는 기기에서도 EXIF GPS 좌표만으로 판정하기 위해 사용한다.
    private const val GEUMTO_MIN_LAT = 37.40203
    private const val GEUMTO_MAX_LAT = 37.42203
    private const val GEUMTO_MIN_LON = 127.06225
    private const val GEUMTO_MAX_LON = 127.10500

    fun startOfTodayMillis(nowMillis: Long, zoneId: ZoneId): Long =
        Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun isBeforeToday(takenMillis: Long, nowMillis: Long, zoneId: ZoneId): Boolean =
        takenMillis in 1 until startOfTodayMillis(nowMillis, zoneId)

    fun isWithinGeumtoArea(latitude: Double, longitude: Double): Boolean =
        latitude in GEUMTO_MIN_LAT..GEUMTO_MAX_LAT &&
            longitude in GEUMTO_MIN_LON..GEUMTO_MAX_LON

    fun isGeumtoAddress(parts: List<String?>): Boolean {
        val text = parts.filterNotNull().joinToString(" ")
        val geumto = text.contains("금토동") || text.contains("Geumto-dong", ignoreCase = true)
        val seongnam = text.contains("성남") || text.contains("Seongnam", ignoreCase = true)
        val sujeong = text.contains("수정구") || text.contains("Sujeong", ignoreCase = true)
        return geumto && seongnam && sujeong
    }
}
