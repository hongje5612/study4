package org.example.annotation

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Company(val name : String)

@Company("Samsung")
class Television(val modelName : String) {

    fun printInfo() {
        val annotation = this::class.annotations.firstOrNull { it.annotationClass == Company::class } ?: return
        val company = annotation as Company
        println("Compay : ${company.name} Model : ${modelName}")
    }

}

/*
@Company("LG")
class Television(val modelName : String) {

    fun printInfo() {
        val annotation = this::class.annotations.firstOrNull { it.annotationClass == Company::class } ?: return
        val company = annotation as Company
        println("Compay : ${company.name} Model : ${modelName}")
    }

}
*/

@Repeatable
annotation class Description(val content : String)


@Description("fast")
@Description("intelligent")
@Description("Gorgeous")
class Car {
    override fun toString(): String {
        val buffer = StringBuilder()
        this::class.annotations.filter { it.annotationClass == Description::class }.forEach {
            buffer.append((it as Description).content)
            buffer.appendLine()
        }
        return buffer.toString()
    }
}



annotation class Tag

class Foo(@get:Tag val name : String)   // get method 에 붙이는 애노테이션
class Bar(@field:Tag val name : String) // name 에 붙이는 애노테이션


annotation class Label(val values : IntArray)

@Label(intArrayOf(1, 2, 3, 4))
class A

@Label([1, 2, 3])
class B

@Target(AnnotationTarget.FUNCTION)
annotation class Executable(val clazz : KClass<*>)

class C {

    @Executable(Int::class)
    fun testInt(value : Int) {
        val result = value > 0
        println("Call : fun testInt($value)")
    }

    @Executable(Double::class)
    fun testDouble(value : Double) {
        val result = value > 0.0
        println("Call : fun testDouble($value)")
    }
}

class D {
    @Executable(Int::class)
    fun incInt(value : Int) {
        val result = value + 1
        println("Call : fun incInt($value)")
    }

    @Executable(Double::class)
    fun incDouble(value : Double) {
        val result = value + 1.0
        println("Call : fun decInt($value)")
    }
}

fun execute(vararg objs : Any) {
    for(obj in objs) execute(obj)
}

fun execute(obj : Any) {
    obj::class.members.filter { it.hasAnnotation<Executable>() }.forEach {
        val annotation = it.findAnnotation<Executable>()
        val exe = annotation as Executable
        when(exe.clazz) {
            Int::class -> {
                it.call(obj, 10)
            }

            Double::class -> {
                it.call(obj, 10.0)
            }

            else -> {

            }
        }
    }
}

fun main()   {
    val car = Car()
    println(car)

    execute(C(), D())
}
