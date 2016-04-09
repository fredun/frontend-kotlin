package com.fredun.frontend.sexpr

import java.util.*

abstract class SExpr {
	override fun toString(): String {
		return gen(this.javaClass, "")
	}

	private fun gen(clazz: Class<*>, childStr: String): String {
		val annotation = clazz.getDeclaredAnnotation(SExprSerialize::class.java) ?: return childStr

		val sb = StringBuilder()
		sb.append('(').append(annotation.name).append(' ')

		val fieldValues = mutableListOf<String>()
		mapFields(clazz, this, annotation.fields) { value -> fieldValues.add(parseValueAsString(value)) }

		if (childStr.isNotBlank()) {
			fieldValues.add(childStr)
		}
		sb.append(fieldValues.joinToString(" ")).append(')')

		val result = sb.toString()
		if (clazz != Any::class.java) {
			return gen(clazz.superclass, result)
		} else {
			return result
		}
	}

	private fun parseValueAsString(value: Any): String {
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

	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (javaClass != other.javaClass) return false

		val valuesThis = getExportedFields(this.javaClass, this, emptyList())
		val valuesOther = getExportedFields(this.javaClass, other, emptyList())

		for (j in 0..valuesThis.size - 1) {
			if (!Objects.equals(valuesThis[j], valuesOther[j])) {
				return false
			}
		}
		return true
	}

	override fun hashCode(): Int {
		val values = getExportedFields(this.javaClass, this, emptyList())
		return Objects.hash(*values)
	}

	private fun getExportedFields(clazz: Class<*>, instance: Any, childFields: List<Any>): Array<Any> {
		if (clazz == Any::class.java) {
			return childFields.toTypedArray()
		}

		val annotation = clazz.getDeclaredAnnotation(SExprSerialize::class.java) ?: return getExportedFields(clazz.superclass, instance, childFields)
		val myFields = mutableListOf<Any>()
		myFields.addAll(childFields)
		mapFields(clazz, instance, annotation.fields) { myFields.add(it) }

		return getExportedFields(clazz.superclass, instance, myFields)
	}

	private fun mapFields(clazz: Class<*>, instance: Any, fields: Array<String>, callback: (value: Any) -> Unit) {
		for (fieldName in fields) {
			val field = clazz.getDeclaredField(fieldName)
			val oldAccessibility = field.isAccessible
			field.isAccessible = true
			val value = field.get(instance)
			field.isAccessible = oldAccessibility

			callback(value)
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
class SExprConstantInt(val value: Long) : SExprConstant()
@SExprSerialize(name = "numeric", fields = arrayOf("value"))
class SExprConstantFloat(val value: Double) : SExprConstant()
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
