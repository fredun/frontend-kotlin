package com.fredun.frontend

import com.fredun.frontend.sexpr.SExprAbstraction
import com.fredun.frontend.sexpr.SExprApplication
import com.fredun.frontend.sexpr.SExprConstantBoolean
import com.fredun.frontend.sexpr.SExprConstantChar
import com.fredun.frontend.sexpr.SExprConstantNumber
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
		SExprConstantNumber(42).apply {
			assertEquals("(constant (numeric 42))", this.toString())
		}
		SExprConstantNumber(42.6659).apply {
			assertEquals("(constant (numeric 42.6659))", this.toString())
		}
		SExprConstantNumber(42e14).apply {
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
		SExprApplication(SExprVariable("lucas"), SExprConstantString("Oeoe")).apply {
			assertEquals("(application (variable \"lucas\") (constant (string \"Oeoe\")))", this.toString())
		}
	}

	@Test
	fun testOperations() {
		SExprOperationBinary("+", SExprConstantNumber(42), SExprConstantString("Universe")).apply {
			assertEquals("(operation (binary \"+\" (constant (numeric 42)) (constant (string \"Universe\"))))", this.toString())
		}
		SExprOperationUnary("-", SExprConstantNumber(42)).apply {
			assertEquals("(operation (unary \"-\" (constant (numeric 42))))", this.toString())
		}
	}

	@Test
	fun testTuple() {
		SExprTuple(SExprConstantNumber(42), SExprConstantString("Universe")).apply {
			assertEquals("(tuple (constant (numeric 42)) (constant (string \"Universe\")))", this.toString())
		}
	}

	@Test
	fun testType() {
		SExprType(SExprConstantNumber(42)).apply {
			assertEquals("(type (constant (numeric 42)))", this.toString())
		}
	}
}