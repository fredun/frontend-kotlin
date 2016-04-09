package com.fredun.frontend.parser

import com.fredun.frontend.sexpr.SExpr
import com.fredun.frontend.sexpr.SExprConstant
import com.fredun.frontend.sexpr.SExprConstantChar
import com.fredun.frontend.sexpr.SExprConstantFloat
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprExpr
import com.fredun.frontend.sexpr.SExprLet
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.FileReader

object SExprParser {
	fun parse(input: String): List<SExpr> {
		return parse(ANTLRInputStream(input))
	}

	fun parse(file: File): List<SExpr> {
		return parse(ANTLRInputStream(FileReader(file)))
	}

	private fun parse(antlrInputStream: ANTLRInputStream): List<SExpr> {
		val lexer = FredunLexer(antlrInputStream)
		val tokens = CommonTokenStream(lexer);
		val parser = FredunParser(tokens);

		return toSExpr(parser.start())
	}

	private fun toSExpr(start: FredunParser.StartContext): List<SExpr> {
		return start.defs().map { toSExpr(it) }
	}

	private fun toSExpr(def: FredunParser.DefsContext): SExpr {
		val child = def.getChild(0)
		return when (child) {
			is FredunParser.LetDefContext -> toSExpr(child)
			else -> throw UnsupportedOperationException()
		}
	}

	private fun toSExpr(let: FredunParser.LetDefContext): SExprLet {
		return SExprLet(let.varName().text, toSExpr(let.expr()))
	}

	private fun toSExpr(expr: FredunParser.ExprContext): SExprExpr {
		return when (expr) {
			is FredunParser.NumberExprContext -> toSExpr(expr)
			is FredunParser.CharExprContext -> toSExpr(expr)
			is FredunParser.StringExprContext -> toSExpr(expr)
			else -> throw UnsupportedOperationException()
		}
	}

	private fun toSExpr(expr: FredunParser.NumberExprContext): SExprConstant {
		val number = expr.number()
		return when (number) {
			is FredunParser.IntNumberContext, is FredunParser.HexNumberContext -> SExprConstantInt(java.lang.Long.valueOf(number.text))
			is FredunParser.FloatNumberContext -> SExprConstantFloat(java.lang.Double.valueOf(number.text))
			else -> throw UnsupportedOperationException()
		}
	}

	private fun toSExpr(expr: FredunParser.CharExprContext): SExprConstantChar {
		return SExprConstantChar(expr.text[0])  // FIXME: This is wrong when the character is a unicode escape code or any other multi-character char
	}

	private fun toSExpr(expr: FredunParser.StringExprContext): SExprConstantString {
		val text = expr.text
		return SExprConstantString(text.substring(1, text.length - 1))
	}
}
