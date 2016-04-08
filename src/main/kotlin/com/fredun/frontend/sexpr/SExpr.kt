package com.fredun.frontend.sexpr

abstract class SExpr {
	override fun toString(): String {
		val javaClass = this.javaClass

		return gen(javaClass, "")
	}

	private fun gen(javaClass: Class<*>, childStr:String): String {
		val annotation = javaClass.getDeclaredAnnotation(SExprSerialize::class.java) ?: return childStr

		val sb = StringBuilder()
		sb.append('(').append(annotation.name).append(' ')

		val fieldValues = mutableListOf<String>()
		for (fieldName in annotation.fields) {
			val field = javaClass.getDeclaredField(fieldName)
			val oldAccessibility = field.isAccessible
			field.isAccessible = true
			val value = field.get(this)
			field.isAccessible = oldAccessibility

			fieldValues.add(parseValueAsString(value))
		}
		if (childStr.isNotBlank()) {
			fieldValues.add(childStr)
		}
		sb.append(fieldValues.joinToString(" ")).append(')')

		val result = sb.toString()
		if (javaClass != Any::class.java) {
			return gen(javaClass.superclass, result)
		} else {
			return result
		}
	}

	private fun parseValueAsString(value: Any):String {
		val javaClass = value.javaClass
		return when (javaClass) {
			String::class.java, java.lang.String::class.java -> "\"$value\""
			Char::class.java, Character::class.java -> "'$value'"
			else -> {
				if (javaClass.isArray) {
					(value as Array<*>).map { parseValueAsString(it!!) }.joinToString(" ")
				} else {
					value.toString()
				}
			}
		}
	}
}

@SExprSerialize(name = "let", fields = arrayOf("name", "expr"))
class SExprLet(val name: String, val expr: SExprExpr) : SExpr()

abstract class SExprExpr : SExpr()

@SExprSerialize(name = "variable", fields = arrayOf("value"))
class SExprVariable(val value: String) : SExprExpr()

@SExprSerialize(name = "constant", fields = arrayOf())
abstract class SExprConstant : SExprExpr()

@SExprSerialize(name = "numeric", fields = arrayOf("value"))
class SExprConstantNumber(val value: Number) : SExprConstant()
@SExprSerialize(name = "char", fields = arrayOf("value"))
class SExprConstantChar(val value: Char) : SExprConstant()
@SExprSerialize(name = "boolean", fields = arrayOf("value"))
class SExprConstantBoolean(val value: Boolean) : SExprConstant()
@SExprSerialize(name = "string", fields = arrayOf("value"))
class SExprConstantString(val value: String) : SExprConstant()

@SExprSerialize(name = "abstraction", fields = arrayOf("values"))
class SExprAbstraction(vararg val values: SExprExpr) : SExprExpr()
@SExprSerialize(name = "application", fields = arrayOf("values"))
class SExprApplication(vararg val values: SExprExpr) : SExprExpr()

@SExprSerialize(name = "operation", fields = arrayOf())
abstract class SExprOperation() : SExprExpr()
@SExprSerialize(name = "binary", fields = arrayOf("kind", "left", "right"))
class SExprOperationBinary(val kind: String, val left: SExprExpr, val right: SExprExpr) : SExprOperation()
@SExprSerialize(name = "unary", fields = arrayOf("kind", "value"))
class SExprOperationUnary(val kind: String, val value: SExprExpr) : SExprOperation()

@SExprSerialize(name = "tuple", fields = arrayOf("values"))
class SExprTuple(vararg val values: SExprExpr) : SExprExpr()

@SExprSerialize(name = "type", fields = arrayOf("value"))
class SExprType(val value: SExprExpr) : SExprExpr()


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SExprSerialize(val name: String, val fields: Array<String>)
