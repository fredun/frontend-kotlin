package com.fredun.frontend

import com.fredun.frontend.sexpr.SExprAbstraction
import com.fredun.frontend.sexpr.SExprApplication
import com.fredun.frontend.sexpr.SExprConstantBoolean
import com.fredun.frontend.sexpr.SExprConstantChar
import com.fredun.frontend.sexpr.SExprConstantFloat
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprLet
import com.fredun.frontend.sexpr.SExprOperationBinary
import com.fredun.frontend.sexpr.SExprOperationUnary
import com.fredun.frontend.sexpr.SExprTuple
import com.fredun.frontend.sexpr.SExprType
import com.fredun.frontend.sexpr.SExprVariable
import org.junit.Assert.assertEquals
import org.junit.Test

class TestSExpr {
	@Test
	fun testVariable() {
		SExprVariable("myAwesomeVar").apply {
			assertEquals("(variable \"myAwesomeVar\")", this.toString())
		}
	}

	@Test
	fun testConstant() {
		SExprConstantInt(42).apply {
			assertEquals("(constant (numeric 42))", this.toString())
		}
		SExprConstantFloat(42.0).apply {
			assertEquals("(constant (numeric 42.0))", this.toString())
		}
		SExprConstantFloat(42.6659).apply {
			assertEquals("(constant (numeric 42.6659))", this.toString())
		}
		SExprConstantFloat(42e14).apply {
			assertEquals("(constant (numeric 4.2E15))", this.toString())
		}
		SExprConstantChar('X').apply {
			assertEquals("(constant (char 'X'))", this.toString())
		}
		SExprConstantBoolean(true).apply {
			assertEquals("(constant (boolean true))", this.toString())
		}
		SExprConstantString("Everything is AWESOME!").apply {
			assertEquals("(constant (string \"Everything is AWESOME!\"))", this.toString())
		}
	}

	@Test
	fun testLet() {
		SExprLet("myThing", SExprConstantString("Oeoe")).apply {
			assertEquals("(let \"myThing\" (constant (string \"Oeoe\")))", this.toString())
		}
	}

	@Test
	fun testAbstraction() {
		SExprAbstraction(SExprVariable("lucas"), SExprConstantString("Oeoe")).apply {
			assertEquals("(abstraction (variable \"lucas\") (constant (string \"Oeoe\")))", this.toString())
		}
	}

	@Test
	fun testApplication() {
		SExprApplication("myFunc", SExprVariable("lucas"), SExprConstantString("Oeoe")).apply {
			assertEquals("(application \"myFunc\" (variable \"lucas\") (constant (string \"Oeoe\")))", this.toString())
		}
	}

	@Test
	fun testOperations() {
		SExprOperationBinary("+", SExprConstantInt(42), SExprConstantString("Universe")).apply {
			assertEquals("(operation (binary \"+\" (constant (numeric 42)) (constant (string \"Universe\"))))", this.toString())
		}
		SExprOperationUnary("-", SExprConstantInt(42)).apply {
			assertEquals("(operation (unary \"-\" (constant (numeric 42))))", this.toString())
		}
	}

	@Test
	fun testTuple() {
		SExprTuple(SExprConstantInt(42), SExprConstantString("Universe")).apply {
			assertEquals("(tuple (constant (numeric 42)) (constant (string \"Universe\")))", this.toString())
		}
	}

	@Test
	fun testType() {
		SExprType(SExprConstantInt(42)).apply {
			assertEquals("(type (constant (numeric 42)))", this.toString())
		}
	}

	@Test
	fun testEquals() {
		assertEquals(
				SExprOperationBinary("+", SExprConstantInt(42), SExprConstantString("Universe")),
				SExprOperationBinary("+", SExprConstantInt(42), SExprConstantString("Universe"))
		)
	}
}
