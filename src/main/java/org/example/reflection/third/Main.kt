package org.example.reflection.third

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class FirstArgument(val clazz : KClass<*>)

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class SecondArgument(val clazz : KClass<*>)

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class ThirdArgument(val clazz : KClass<*>)

class Point(val x : Double, val y : Double) {
    override fun toString(): String {
        return "($x, $y)"
    }
}

class Circle(val center : Point, val radius : Double) {
    @FirstArgument(Int::class)
    @SecondArgument(Int::class)
    @ThirdArgument(Double::class)
    constructor(x : Int, y : Int, radius : Double) : this(Point(x.toDouble(), y.toDouble()), radius) {
        println("정수 원점")
    }

    @FirstArgument(Double::class)
    @SecondArgument(Double::class)
    @ThirdArgument(Double::class)
    constructor(x : Double, y : Double, radius : Double) : this(Point(x, y), radius) {
        println("유리수 원점")
    }

    override fun toString(): String {
        return "($center, $radius)"
    }
}

fun makeCircle(x : Any, y : Any, radius : Any) : Circle? {
    val constructor = Circle::class.constructors.filter {
        val condition = it.hasAnnotation<FirstArgument>() && it.hasAnnotation<SecondArgument>() && it.hasAnnotation<ThirdArgument>()
        if(!condition) return@filter false

        val first = it.findAnnotation<FirstArgument>()
        val second = it.findAnnotation<SecondArgument>()
        val third = it.findAnnotation<ThirdArgument>()

        return@filter first!!.clazz == x::class && second!!.clazz == y::class && third!!.clazz == radius::class
    }
        .firstOrNull() ?: return null

    return constructor.call(x, y, radius)
}


fun main() {
    val circle = makeCircle(2, 3, 4.5)
    println(circle)
    val circle2 = makeCircle(2.0, 2.0, 4.5)
    println(circle2)
}