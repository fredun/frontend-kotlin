package com.fredun.frontend

import com.fredun.frontend.parser.SExprParser
import java.io.File

fun main(args: Array<String>) {
	if (args.size < 1) {
		System.err?.println("Usage: ./frontend file.fn")
		return
	}

	val file = File(args[0])
	if (!file.exists()) {
		System.err?.println("ERROR: File \"$file\" doesn't exist")
	}

	val sexprs = SExprParser.parse(file)

	println(sexprs[0])
	// FIXME: Reenable when backend can accept multiple lines
	/*println("(")
	sexprs.forEach { println("\t$it") }
	println(")")*/
}
