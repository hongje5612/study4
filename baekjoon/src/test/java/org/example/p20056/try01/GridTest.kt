package org.example.p20056.try01

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.abs

class GridTest {
    private val size = 4

    private fun Byte.nextLocation(direction : Direction, speed : Short) : Byte {
        fun Byte.next(condition : Boolean, speed : Short) : Byte {
            when(condition) {
                // 처음에 증가하는 방향
                true -> {
                    val location = this + speed
                    val rem = location % size
                    return rem.toByte()
                }
                // 처음에 감소하는 방향
                false -> {
                    val location = this - speed
                    if(location >= 0) return location.toByte()
                    val rem = abs(location) % size
                    if(rem != 0) return (size - rem).toByte() else return 0
                }
            }
        }

        when(direction) {
            Direction.NORTH -> return this.next(false, speed)
            Direction.SOUTH -> return this.next(true, speed)
            Direction.WEST -> return this.next(false, speed)
            Direction.EAST -> return this.next(true, speed)
            else -> throw IllegalArgumentException("허용되지 않는 방향입니다.")
        }
    }

    @Test
    fun checkByteValue() {
        var curr : Byte = 1

        /*
        for(speed in 1..10) {
            val nextLocation = curr.nextLocation(Direction.EAST, speed.toShort())
            println(nextLocation)
        }
        */

        for(speed in 1..10) {
            val nextLocation = curr.nextLocation(Direction.WEST, speed.toShort())
            println(nextLocation)
        }
    }
}