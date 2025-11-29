package org.example.reflection.first

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ShouldBeCalled

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Important

class Foo(val amount : Int) {
    constructor(amount : Byte) : this(amount.toInt())
    constructor(amount : Short) : this(amount.toInt())
    constructor(amount : Long) : this(amount.toInt()) {
        if(amount > Int.MAX_VALUE) throw IllegalArgumentException("값이 너무 큽니다.")
        /*
        주 생성자를 콜하기 전에 실행되어야 할 코드를 넣을 수 있는 곳이 없는 것 같다.
         */
    }

    init {
        this::class.members
            .filter {
                val annotation = it.findAnnotation<ShouldBeCalled>() ?: return@filter false
                return@filter true
            }.forEach { it.call(this) }
        println()
    }

    @ShouldBeCalled
    fun printAmount() {
        print("Amount : $amount")
    }

    fun printAmount(more : Int) {
        print("Amount : ${amount + more}")
    }

    fun printImportantValue(v1 : Int, v2 : Int, @Important v3 : Int) {
        val array = arrayOf(v1, v2, v3)
        this::printImportantValue.parameters.forEachIndexed { index, parameter ->
            if(parameter.hasAnnotation<Important>()) {
                this::printValue.call( array[index])
            }
        }
    }

    fun printValue(v : Int) {
        println("value $v")
    }
}

fun main() {
    val clazz = Foo::class

    clazz.members.forEach { println(it.name) }

    clazz.members
        .filter { it.name == "printAmount" && it.parameters.size == 1 }
        .getOrNull(0)
        ?.call(Foo(5)) ?: return
    println()


    clazz.members
        .filter { it.name == "printAmount" && it.parameters.size == 2 }
        .getOrNull(0)
        ?.call(Foo(5), 5) ?: return
    println()

    val callable = clazz.members
                        .filter { it.name == "printAmount" && it.parameters.size == 2 }
                        .getOrNull(0) ?: return

    callable.parameters.forEach { println(it.name) }

    callable.parameters.forEach {
        when(it.kind) {
            KParameter.Kind.INSTANCE -> println("instance")
            KParameter.Kind.VALUE -> println("value")
            KParameter.Kind.EXTENSION_RECEIVER -> println("extension_receiver")
        }
    }

    callable.parameters.forEach {
        val cz = it.type.classifier as KClass<*>
        cz.members.forEach { println(it.name) }
    }

    callable.parameters.forEach {

    }
    println()

    val foo = Foo(1)
    foo.printImportantValue(1, 2, 3)
}