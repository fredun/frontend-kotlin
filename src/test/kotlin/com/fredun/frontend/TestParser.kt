package com.fredun.frontend

import com.fredun.frontend.parser.SExprParser
import com.fredun.frontend.sexpr.SExpr
import com.fredun.frontend.sexpr.SExprApplication
import com.fredun.frontend.sexpr.SExprConstantFloat
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprConstantUnsigned
import com.fredun.frontend.sexpr.SExprFloatBits
import com.fredun.frontend.sexpr.SExprIntegerBits
import com.fredun.frontend.sexpr.SExprLet
import com.fredun.frontend.sexpr.SExprOperationBinary
import com.fredun.frontend.sexpr.SExprVariable
import org.junit.Assert.assertArrayEquals
import org.junit.Ignore
import org.junit.Test

class TestParser {
	@Test
	@Ignore     // FIXME: Remove when backend can parse defs
	fun testLet() {
		testSingle(SExprLet("myThing", SExprConstantInt(42, SExprIntegerBits.I32)), "let myThing = 42\n")
	}

	@Test
	@Ignore     // FIXME: Remove when backend can parse defs
	fun testMultipleLet() {
		val expected = arrayOf(
				SExprLet("myThing", SExprConstantString("Oeoe")),
				SExprLet("myOtherThing", SExprConstantInt(42, SExprIntegerBits.I32))
		)
		val result = SExprParser.parse("let myThing = \"Oeoe\"\nlet myOtherThing = 42\n")

		assertArrayEquals(expected, result.toTypedArray())
	}

	@Test
	// FIXME: everything in this test needs to be wrapped in a `def` after we add them back
	fun testApplication() {
		testSingle(SExprApplication(SExprVariable("myFunc")) ,"myFunc()")
		testSingle(SExprApplication(SExprVariable("myFunc"), SExprConstantInt(42, SExprIntegerBits.I32)), "myFunc(42)")

		testSingle(
				SExprApplication(SExprVariable("myFunc"), SExprConstantString("Sparta!"), SExprConstantFloat(42.42, SExprFloatBits.F32)),
				"myFunc(\"Sparta!\", 42.42)"
		)
	}

	@Test
	fun testNumericIntegerSuffixes() {
		testSingle(SExprConstantInt(42, SExprIntegerBits.I32), "42\n")

		testSingle(SExprConstantInt(42, SExprIntegerBits.I8), "42i8\n")
		testSingle(SExprConstantInt(42, SExprIntegerBits.I16), "42i16\n")
		testSingle(SExprConstantInt(42, SExprIntegerBits.I32), "42i32\n")
		testSingle(SExprConstantInt(42, SExprIntegerBits.I64), "42i64\n")

		testSingle(SExprConstantUnsigned(42, SExprIntegerBits.I8), "42u8\n")
		testSingle(SExprConstantUnsigned(42, SExprIntegerBits.I16), "42u16\n")
		testSingle(SExprConstantUnsigned(42, SExprIntegerBits.I32), "42u32\n")
		testSingle(SExprConstantUnsigned(42, SExprIntegerBits.I64), "42u64\n")
	}

	@Test
	fun testNumericFloatSuffixes() {
		testSingle(SExprConstantFloat(42.0, SExprFloatBits.F32), "42.0")
		testSingle(SExprConstantFloat(42.0, SExprFloatBits.F64), "42f64")
		testSingle(SExprConstantFloat(42.0, SExprFloatBits.F64), "42.0f64")
		testSingle(SExprConstantFloat(42.0, SExprFloatBits.F64), "0x2Af64")
	}

	@Test
	fun testBinaryOperations() {
		// Additive
		testSingle(SExprOperationBinary("+", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 + \"Universe\"")
		testSingle(SExprOperationBinary("-", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 - \"Universe\"")

		// Multiplicative
		testSingle(SExprOperationBinary("*", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 * \"Universe\"")
		testSingle(SExprOperationBinary("/", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 / \"Universe\"")
		testSingle(SExprOperationBinary("%", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 % \"Universe\"")

		// Equality
		testSingle(SExprOperationBinary("==", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 == \"Universe\"")
		testSingle(SExprOperationBinary("!=", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 != \"Universe\"")

		// Relative
		testSingle(SExprOperationBinary("<=", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 <= \"Universe\"")
		testSingle(SExprOperationBinary("<", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 < \"Universe\"")
		testSingle(SExprOperationBinary(">=", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 >= \"Universe\"")
		testSingle(SExprOperationBinary(">", SExprConstantInt(42, SExprIntegerBits.I32), SExprConstantString("Universe")), "42 > \"Universe\"")
	}

	private fun testSingle(expected: SExpr, input: String) {
		val result = SExprParser.parse(input)
		assertArrayEquals(arrayOf(expected), result.toTypedArray())
	}
}
