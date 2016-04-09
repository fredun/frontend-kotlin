package com.fredun.frontend

import com.fredun.frontend.parser.SExprParser
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprLet
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class TestParser {
	@Test
	fun testLet() {
		val expected = arrayOf(SExprLet("myThing", SExprConstantString("Oeoe")))
		val result = SExprParser.parse("let myThing = \"Oeoe\"\n")

		assertArrayEquals(expected, result.toTypedArray())
	}
}
