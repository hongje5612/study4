package org.example.reflection

import org.example.reflection.first.Foo

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Executable

class Foo(val value : Int) {

    fun print() {
        println(value)
    }

    @Executable
    fun print2X() {
        println(value * 2)
    }

}

fun execute(foo : Foo) {
    val callable = foo::class.members.firstOrNull { it.annotations.contains(Executable()) } ?: return
    callable.call(foo)
}

fun main() {
    val foo = Foo(10)

    execute(foo)
}