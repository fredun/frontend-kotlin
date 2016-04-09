package com.fredun.frontend.parser

import com.fredun.frontend.sexpr.SExpr
import com.fredun.frontend.sexpr.SExprApplication
import com.fredun.frontend.sexpr.SExprConstant
import com.fredun.frontend.sexpr.SExprConstantChar
import com.fredun.frontend.sexpr.SExprConstantFloat
import com.fredun.frontend.sexpr.SExprConstantInt
import com.fredun.frontend.sexpr.SExprConstantString
import com.fredun.frontend.sexpr.SExprConstantUnsigned
import com.fredun.frontend.sexpr.SExprExpr
import com.fredun.frontend.sexpr.SExprFloatBits
import com.fredun.frontend.sexpr.SExprIntegerBits
import com.fredun.frontend.sexpr.SExprLet
import com.fredun.frontend.sexpr.SExprVariable
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
//		return start.defs().map { toSExpr(it) }
		// FIXME: Remove this when backend can parse defs
		return start.expr().map { toSExpr(it) }
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
			is FredunParser.IdExprContext -> toSExpr(expr)
			is FredunParser.NumberExprContext -> toSExpr(expr)
			is FredunParser.CharExprContext -> toSExpr(expr)
			is FredunParser.StringExprContext -> toSExpr(expr)
			is FredunParser.FuncApplicationExprContext -> toSExpr(expr)
			else -> throw UnsupportedOperationException(expr.javaClass.canonicalName)
		}
	}

	private fun toSExpr(expr: FredunParser.IdExprContext): SExprVariable {
		return SExprVariable(expr.text)
	}

	private fun toSExpr(expr: FredunParser.NumberExprContext): SExprConstant {
		// FIXME: Parse suffixes
		val number = expr.number()
		val text = number.text
		return when (number) {
			is FredunParser.IntNumberContext, is FredunParser.HexNumberContext -> {
				// FIXME: This needs to be improved on the grammar itself
				val signAndBits = when {
					text.endsWith("i8") -> false to SExprIntegerBits.I8
					text.endsWith("i16") -> false to SExprIntegerBits.I16
					text.endsWith("i32") -> false to SExprIntegerBits.I32
					text.endsWith("i64") -> false to SExprIntegerBits.I64
					text.endsWith("u8") -> true to SExprIntegerBits.I8
					text.endsWith("u16") -> true to SExprIntegerBits.I16
					text.endsWith("u32") -> true to SExprIntegerBits.I32
					text.endsWith("u64") -> true to SExprIntegerBits.I64
					else -> false to SExprIntegerBits.I32
				}
				if (signAndBits.first) {
					SExprConstantUnsigned(java.lang.Long.valueOf(text), signAndBits.second)
				} else {
					SExprConstantInt(java.lang.Long.valueOf(text), signAndBits.second)
				}
			}
			is FredunParser.FloatNumberContext -> {
				// FIXME: This needs to be improved on the grammar itself
				val bits = when {
					text.endsWith("f32") -> SExprFloatBits.F32
					text.endsWith("f64") -> SExprFloatBits.F64
					else -> SExprFloatBits.F32
				}
				SExprConstantFloat(java.lang.Double.valueOf(number.text), bits)
			}
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

	private fun toSExpr(expr: FredunParser.FuncApplicationExprContext): SExprApplication {
		val exprs = expr.expr()
		val func = exprs[0]
		val args = exprs.drop(1).map { toSExpr(it) }
		return SExprApplication(toSExpr(func), *args.toTypedArray())
	}
}
