package org.example.reflection

import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

fun isOdd(value : Int) : Boolean {
    return value % 2 == 1
}

fun isGreater(n1 : Int, n2 : Int) : Boolean {
    return n1 > n2
}

val x = 1
var y = 2

val String.lastChar: Char
    get() = this[length - 1]

class A(val p: Int)

fun main() {

    println(::x.get())
    ::y.set(4)
    println(::y.get())
    println()

    class A(val p: Int)
    val prop = A::p
    println(prop.get(A(1)))
    println()

    println(String::lastChar.get("abc"))
    println()

    println(A::p.javaGetter) // prints "public final int A.getP()"
    println(A::p.javaField)  // prints "private final int A.p"

    val function  = ::isOdd

    val list = listOf(1, 2, 3, 4, 5)

    list.filter(::isOdd).forEach { println(it) }

    val function2 = ::isGreater
    list.filter { isGreater(it, 3) }.forEach { println(it) }

}