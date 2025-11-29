package org.example.reflection.forth

import kotlin.random.Random
import kotlin.reflect.KCallable
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

interface Movable {
    fun turnLeft()
    fun turnRight()
    fun goForward()
}

class Location(var x : Int, var y : Int)

enum class Direction {
    EAST, WEST, SOUTH, NORTH
}

@Target(AnnotationTarget.FUNCTION)
annotation class Percentage(val value : Int)

var countOfTurningLeft = 0
var countOfTurningRight = 0
var countOfGoingForward = 0

class Robot(val currentLocation : Location, var currentDirection : Direction) : Movable {
    @Percentage(10)
    override fun turnLeft() {
        countOfTurningLeft++
    }

    @Percentage(10)
    override fun turnRight() {
        countOfTurningRight++
    }

    @Percentage(80)
    override fun goForward() {
        countOfGoingForward++
    }
}

val random = Random(System.currentTimeMillis())

fun Robot.move() {
    val callables = this::class.members.filter { it.hasAnnotation<Percentage>() }
    val map = callables.associateWith { it.findAnnotation<Percentage>()!!.value }
    val m = HashMap<IntRange, KCallable<*>>()
    var start = 1
    for(entry in map) {
        val end = start + entry.value
        m.put(IntRange(start, end), entry.key)
        start = end
    }
    val randomNumber = random.nextInt(1, start)

    for(entry in m) {
        if(randomNumber in entry.key) entry.value.call(this)
    }
}

fun main() {

    val robot = Robot(Location(0, 0), Direction.EAST)

    for(i in 0 until 100) {
        robot.move()
    }

    println("count of turning left : $countOfTurningLeft")
    println("count of turning right : $countOfTurningRight")
    println("count of going forward : $countOfGoingForward")
}