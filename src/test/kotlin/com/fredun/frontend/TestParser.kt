package com.fredun.frontend

import com.fredun.frontend.parser.SExprParser
import com.fredun.frontend.sexpr.SExprApplication
import com.fredun.frontend.sexpr.SExprConstantFloat
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprLet
import com.fredun.frontend.sexpr.SExprVariable
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

	@Test
	fun testApplication() {
		run {
			// FIXME: This needs to be wrapped in a `def` after we add them back
			val expected = arrayOf(
					SExprApplication(SExprVariable("myFunc"))
			)
			val result = SExprParser.parse("myFunc()")

			assertArrayEquals(expected, result.toTypedArray())
		}
		run {
			// FIXME: This needs to be wrapped in a `def` after we add them back
			val expected = arrayOf(
					SExprApplication(SExprVariable("myFunc"), SExprConstantInt(42))
			)
			val result = SExprParser.parse("myFunc(42)")

			assertArrayEquals(expected, result.toTypedArray())
		}
		run {
			// FIXME: This needs to be wrapped in a `def` after we add them back
			val expected = arrayOf(
					SExprApplication(SExprVariable("myFunc"), SExprConstantString("Sparta!"), SExprConstantFloat(42.42))
			)
			val result = SExprParser.parse("myFunc(\"Sparta!\", 42.42)")

			assertArrayEquals(expected, result.toTypedArray())
		}
	}
}
