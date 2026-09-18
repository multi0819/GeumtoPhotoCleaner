package com.example.geumtophotocleaner

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoRulesTest {
    @Test
    fun `금토동 중심 좌표는 주소에 동 이름이 없어도 찾는다`() {
        assertTrue(PhotoRules.isWithinGeumtoArea(37.41203, 127.07225))
    }

    @Test
    fun `판교 제2테크노밸리 동쪽 금토동 좌표도 찾는다`() {
        assertTrue(PhotoRules.isWithinGeumtoArea(37.4088884, 127.1001891))
    }

    @Test
    fun `수정구이지만 금토동에서 벗어난 좌표는 제외한다`() {
        assertFalse(PhotoRules.isWithinGeumtoArea(37.44680, 127.13890))
    }
}
