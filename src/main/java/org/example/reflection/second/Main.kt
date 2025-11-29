package org.example.reflection.second

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class ByteValue

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class IntValue

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class ShortValue

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class LongValue


class Foo @IntValue constructor(val amount : Int) {

    @ByteValue
    constructor(byteAmount : Byte) : this(byteAmount.toInt())

    @ShortValue
    constructor(shortAmount : Short) : this(shortAmount.toInt())

    @LongValue
    constructor(longAmount : Long) : this(longAmount.toInt()) {
        if(longAmount > Int.MAX_VALUE) throw IllegalArgumentException("너무 큰 수를 전달했습니다. $longAmount")
    }

    fun print() {
        println(amount)
    }

    operator fun inc() : Foo {
        return Foo(amount + 1)
    }

    operator fun dec() : Foo {
        return Foo(amount - 1)
    }
}

fun makeFoo(clazz : KClass<*>) : List<Foo> {
    val intValues = intArrayOf(1, 2, 3, 4, 5)
    val byteValues = byteArrayOf(4, 5, 6, 7, 8)
    val shortValues = shortArrayOf( 4, 4, 5, 5, 3)
    val longValues = longArrayOf(3L, 4L, 5L, 8L)

    val constructors = Foo::class.constructors.filter { kFunction ->
        when(clazz) {
            Int::class -> {
                return@filter kFunction.hasAnnotation<IntValue>()
            }
            Short::class -> {
                return@filter kFunction.hasAnnotation<ShortValue>()
            }
            Byte::class -> {
                return@filter kFunction.hasAnnotation<ByteValue>()
            }
            Long::class -> {
                return@filter kFunction.hasAnnotation<LongValue>()
            }
            else -> return@filter false
        }
    }

    val result = ArrayList<Foo>()

    when(clazz) {
        Int::class -> {
            constructors.forEach {
                for(n in intValues) {
                    result.add(it.call(n))
                }
            }
        }

        Byte::class -> {
            constructors.forEach {
                for(n in byteValues) {
                    result.add(it.call(n))
                }
            }
        }

        Short::class -> {
            constructors.forEach {
                for(n in shortValues) {
                    result.add(it.call(n))
                }
            }
        }

        Long::class -> {
            constructors.forEach {
                for(n in longValues) {
                    result.add(it.call(n))
                }
            }
        }

        else -> {}
    }
    return result
}




fun main() {
    Foo::class.constructors.forEach { println(it.name) }

    val foos = makeFoo(Int::class)
    foos.forEach { it.print() }

    makeFoo(Long::class).forEach { it.print() }

}