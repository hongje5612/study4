package org.example.reflection.sixth

import org.reflections.Reflections
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

open class Foo {
    open fun show() {
        println("I am Foo")
    }
}

class Bar : Foo() {
    override fun show() {
        println("I am Bar")
    }
}

annotation class MyAnno

@MyAnno
class AService {
    fun show() {
        println("AService 입니다.")
    }
}

@MyAnno
class BService(val aService : AService) {
    fun show() {
        aService.show()
    }
}

/*
fun main() {
    ContainerV1.register(Foo::class)
    ContainerV1.register(Bar::class)
    ContainerV1.getInstance(Bar::class)?.show() ?: println("null ???.")
    println("** end **")
}
 */

/*
fun main() {
    ContainerV2.register(AService::class)
    ContainerV2.register(BService::class)

    val bService = ContainerV2.instantiate(BService::class)
    bService.show()
}
 */

class DI

fun main() {
    start(DI::class)
    val bService = ContainerV2.getInstance(BService::class)
    bService.show()
}



object ContainerV1 {
    private val registeredClasses = HashSet<KClass<*>>()

    fun register(clazz : KClass<*>) {
        registeredClasses.add(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInstance(type : KClass<T>) : T? {
        return registeredClasses.firstOrNull { it == type }
            ?.let {
                it.constructors.firstOrNull {
                    it.parameters.isEmpty()
                }?.call() as T
            }
            ?: throw IllegalArgumentException("생성자를 찾지 못했습니다.")
    }
}

fun start(clazz : KClass<*>) {
    val reflections = Reflections(clazz.packageName)
    val jClasses = reflections.getTypesAnnotatedWith(MyAnno::class.java)
    jClasses.forEach { jClass -> ContainerV2.register(jClass.kotlin) }
}

private val KClass<*>.packageName : String
    get() {
        val qualifierName = this.qualifiedName
            ?: throw IllegalArgumentException("익명 객체입니다.")
        val ss = qualifierName.split(".")
        return ss.subList(0, ss.lastIndex).joinToString(".")
    }

object ContainerV2 {
    private val registeredClasses = HashSet<KClass<*>>()
    private val chachedInstances = HashMap<KClass<*>, Any>()

    fun register(clazz : KClass<*>) {
        registeredClasses.add(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInstance(type : KClass<T>) : T {
        if(type in chachedInstances) return chachedInstances[type] as T
        val instance = registeredClasses.firstOrNull { it == type }
            ?.let { clazz ->
                instantiate(clazz) as T
            }
            ?: throw IllegalArgumentException("생성하지 못 했습니다.")
        chachedInstances[type] = instance
        return instance
    }

    fun <T: Any> findUsableConstructor(clazz : KClass<T>) : KFunction<T> {
        return clazz.constructors.firstOrNull { constructor ->
            constructor.parameters.all { it.type.classifier in registeredClasses}
        } ?: throw IllegalArgumentException("?? ??? ???? ????.")
    }

    fun <T : Any> instantiate(clazz : KClass<T>) : T {
        val constructor = findUsableConstructor(clazz)
        val param = constructor.parameters.map {
            getInstance(it.type.classifier as KClass<*>)
        }.toTypedArray()
        return constructor.call(*param)
    }
}
