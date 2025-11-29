package org.example.misc.second

class MyClass {
    fun myMethod(value: String) {
        // ...
    }
}

val myInstance = MyClass()
val method = MyClass::myMethod

fun main() {
    method.parameters.forEach {
        println("${it.name}: ${it.kind}")
    }
}