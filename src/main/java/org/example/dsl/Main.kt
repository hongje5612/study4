package org.example.dsl


import kotlin.properties.Delegates


operator fun String.minus(other : String) : DockerCompose.Service.Environment {
    return DockerCompose.Service.Environment(this, other)
}

fun main() {
    val yml = dockerCompose {
        version { 3 }
        service(name = "db") {
            image { "mysql" }
            env ( "USER" - "myuser" )
            env ( "PASSWORD" - "mypassword" )
            port( host = 9999, container = 3306)
        }
    }
    println(yml.render(" "))

    /*
    val yml2 = dockerCompose {
        service(name = "") {
            service("") {       //@DslMarker 때문에 에러가 납니다.
                                //가장 가까운 스코프의 this 만 생략이 가능합니다. 그래서 에러가 납니다.
            }
        }
    }
     */
}

fun dockerCompose(init : DockerCompose.() -> Unit) : DockerCompose {
    val compose = DockerCompose()
    compose.init()
    return compose
}

@YamlDsl
class DockerCompose {
    @YamlDsl
    class Service(val name : String) {
        data class Environment (val key : String, val value : String)
        data class PortRule(val host : Int, val container : Int)

        private lateinit var imageName : String
        private val environments = mutableListOf<Environment>()
        private val portRules = mutableListOf<PortRule>()

        fun image(init : () -> String) {
            imageName = init()
        }

        fun env(environment: DockerCompose.Service.Environment) {
            environments.add(environment)
        }

        fun port(host : Int, container : Int) {
            portRules.add(PortRule(host, container))
        }

        fun render(indent : String) : String {
            val builder = StringBuilder()
            builder.appendNew("$name:")
            builder.appendNew("image : $imageName", indent, 1)
            builder.appendNew("environment:")
            environments.joinToString("\n") { "- ${it.key}: ${it.value}" }.addIndent(indent, 1).also { builder.appendNew(it)}
            builder.appendNew("port:")
            portRules.joinToString { "- \"${it.host}: ${it.container}\"" }.addIndent(indent, 1).also { builder.appendNew(it)}

            return builder.toString()
        }
    }

    private var version : Int by Delegates.notNull()
    private val services = mutableListOf<Service>()

    fun version(init : () -> Int) {
        version = init()
    }

    fun service(name : String , init : Service.() -> Unit) {
        val service = Service(name)
        service.init()
        services.add(service)
    }

    fun render(indent : String) : String {
        val builder = StringBuilder()
        builder.appendNew("version '$version'",)
        builder.appendNew("services : ")
        builder.appendNew(services.joinToString("\n") { it.render(indent) }.addIndent(indent, 1 ))
        return builder.toString()
    }
}

fun StringBuilder.appendNew(str : String, indent : String = "", times : Int = 0) {
    (1..times).forEach { _ -> append(indent) }
    append(str)
    appendLine()
}

fun String.addIndent(indent : String, times : Int = 0) : String {
    val allIndent = (1..times).joinToString { indent }
    return split("\n").joinToString("\n") { "$allIndent$it" }
}

@DslMarker
annotation class YamlDsl