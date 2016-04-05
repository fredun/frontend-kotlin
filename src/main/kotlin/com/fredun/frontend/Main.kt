package com.fredun.frontend

import com.fredun.frontend.parser.FredunLexer
import com.fredun.frontend.parser.FredunParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.*

fun main(args: Array<String>) {
	val input = """
    let x = 555u16 * 4444u32
    let y = 222u64 * 3333u8
    """
	val lexer = FredunLexer(ANTLRInputStream(input))
	val tokens = CommonTokenStream(lexer);
	val parser = FredunParser(tokens);
	val tree = parser.start();
	val walker = ParseTreeWalker();
	walker.walk(FredunWalker(), tree);
}

class FredunWalker : ParseTreeListener {
	override fun enterEveryRule(p0: ParserRuleContext?) {
		println("Enter ${p0?.getStart()}")
	}

	override fun exitEveryRule(p0: ParserRuleContext?) {
		println("Exit ${p0?.getStart()}")
	}

	override fun visitErrorNode(p0: ErrorNode?) {
		println("Error")
	}

	override fun visitTerminal(p0: TerminalNode?) {
		println("Terminal ${p0?.symbol}")
	}
}
