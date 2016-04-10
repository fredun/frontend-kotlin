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
import com.fredun.frontend.sexpr.SExprOperationBinary
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
			is FredunParser.MultiplicationExprContext -> toSExpr(expr)
			is FredunParser.AdditiveExprContext -> toSExpr(expr)
			is FredunParser.RelationalExprContext -> toSExpr(expr)
			is FredunParser.EqualityExprContext -> toSExpr(expr)
			else -> throw UnsupportedOperationException(expr.javaClass.canonicalName)
		}
	}

	private fun toSExpr(expr: FredunParser.IdExprContext): SExprVariable {
		return SExprVariable(expr.text)
	}

	private fun toSExpr(expr: FredunParser.NumberExprContext): SExprConstant {
		val number = expr.number()
		val text = number.text

		// FIXME: This needs to be improved on the grammar itself
		val textIsDouble = number is FredunParser.FloatNumberContext
		var endsWithSuffix = true
		val signAndBits = when {
			text.endsWith("i8") -> false to SExprIntegerBits.I8
			text.endsWith("i16") -> false to SExprIntegerBits.I16
			text.endsWith("i32") -> false to SExprIntegerBits.I32
			text.endsWith("i64") -> false to SExprIntegerBits.I64
			text.endsWith("u8") -> true to SExprIntegerBits.I8
			text.endsWith("u16") -> true to SExprIntegerBits.I16
			text.endsWith("u32") -> true to SExprIntegerBits.I32
			text.endsWith("u64") -> true to SExprIntegerBits.I64
			text.endsWith("f32") -> false to SExprFloatBits.F32
			text.endsWith("f64") -> false to SExprFloatBits.F64
			else -> {
				endsWithSuffix = false
				false to (if (textIsDouble) SExprFloatBits.F32 else SExprIntegerBits.I32)
			}
		}
		val bits = signAndBits.second
		val numberText = if (endsWithSuffix) text.substring(0, text.length - bits.name.length) else text
		val num: Number = when (number) {
			is FredunParser.IntNumberContext, is FredunParser.HexNumberContext -> java.lang.Long.decode(numberText)
			is FredunParser.FloatNumberContext -> java.lang.Double.valueOf(numberText)
			else -> throw UnsupportedOperationException()
		}
		return when (bits) {
			is SExprFloatBits -> SExprConstantFloat(num.toDouble(), bits)
			is SExprIntegerBits -> {
				if (signAndBits.first) {
					SExprConstantUnsigned(num.toLong(), bits)
				} else {
					SExprConstantInt(num.toLong(), bits)
				}
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

	private fun toSExpr(expr: FredunParser.MultiplicationExprContext): SExprOperationBinary {
		return SExprOperationBinary(expr.op.text, toSExpr(expr.expr(0)), toSExpr(expr.expr(1)))
	}

	private fun toSExpr(expr: FredunParser.AdditiveExprContext): SExprOperationBinary {
		return SExprOperationBinary(expr.op.text, toSExpr(expr.expr(0)), toSExpr(expr.expr(1)))
	}

	private fun toSExpr(expr: FredunParser.RelationalExprContext): SExprOperationBinary {
		return SExprOperationBinary(expr.op.text, toSExpr(expr.expr(0)), toSExpr(expr.expr(1)))
	}

	private fun toSExpr(expr: FredunParser.EqualityExprContext): SExprOperationBinary {
		return SExprOperationBinary(expr.op.text, toSExpr(expr.expr(0)), toSExpr(expr.expr(1)))
	}
}
