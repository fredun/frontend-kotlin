package com.fredun.frontend

import com.fredun.frontend.parser.SExprParser
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprLet
import org.junit.Assert.assertArrayEquals
import org.junit.Ignore
import org.junit.Test

class TestParser {
	@Test
	@Ignore     // FIXME: Remove when backend can parse defs
	fun testLet() {
		val expected = arrayOf(SExprLet("myThing", SExprConstantInt(42)))
		val result = SExprParser.parse("let myThing = 42\n")

		assertArrayEquals(expected, result.toTypedArray())
	}

	@Test
	@Ignore     // FIXME: Remove when backend can parse defs
	fun testMultipleLet() {
		val expected = arrayOf(
				SExprLet("myThing", SExprConstantString("Oeoe")),
				SExprLet("myOtherThing", SExprConstantInt(42))
		)
		val result = SExprParser.parse("let myThing = \"Oeoe\"\nlet myOtherThing = 42\n")

		assertArrayEquals(expected, result.toTypedArray())
	}
}
