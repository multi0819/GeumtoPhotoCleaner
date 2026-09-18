package com.example.geumtophotocleaner

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoAccessTest {
    @Test
    fun `전체 권한이 있으면 부분 권한보다 우선한다`() {
        assertEquals(MediaAccess.FULL, PhotoAccess.resolve(fullGranted = true, partialGranted = true))
    }

    @Test
    fun `선택 사진 권한만 있으면 부분 권한이다`() {
        assertEquals(MediaAccess.PARTIAL, PhotoAccess.resolve(fullGranted = false, partialGranted = true))
    }

    @Test
    fun `어떤 사진 권한도 없으면 거부 상태이다`() {
        assertEquals(MediaAccess.DENIED, PhotoAccess.resolve(fullGranted = false, partialGranted = false))
    }
}
