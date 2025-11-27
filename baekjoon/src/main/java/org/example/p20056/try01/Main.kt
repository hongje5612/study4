package org.example.p20056.try01

import kotlin.math.abs

class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())
}

enum class Direction {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
}

fun Int.toDirection() : Direction =
    when(this) {
        0 -> Direction.NORTH
        1 -> Direction.NORTH_EAST
        2 -> Direction.EAST
        3 -> Direction.SOUTH_EAST
        4 -> Direction.SOUTH
        5 -> Direction.SOUTH_WEST
        6 -> Direction.WEST
        7 -> Direction.NORTH_WEST
        else -> throw IllegalArgumentException("방향을 나타내는 숫자는 0~7 까지 입니다. 현제 숫자 : $this")
    }

class FireBall(val mass : Int, val speed : Int, val direction : Direction)

class Grid(val size : Short, val count : Short, board : Array<Array<ArrayList<FireBall>>>) {
    private var grid = board

    private fun Byte.nextLocation(direction : Direction, speed : Int) : Byte {
        fun Byte.next(condition : Boolean, speed : Int) : Byte {
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
            Direction.NORTH, Direction.WEST -> return this.next(false, speed)
            Direction.SOUTH, Direction.EAST -> return this.next(true, speed)
            else -> throw IllegalArgumentException("허용되지 않는 방향입니다.")
        }
    }

    private fun Location.nextLocation(direction : Direction, speed : Int) : Location {
        var row : Byte
        var col : Byte

        when(direction) {
            Direction.NORTH -> {
                row = this.row.nextLocation(Direction.NORTH, speed)
                col = this.col
            }

            Direction.NORTH_EAST -> {
                row = this.row.nextLocation(Direction.NORTH, speed)
                col = this.col.nextLocation(Direction.EAST, speed)
            }

            Direction.EAST -> {
                row = this.row
                col = this.col.nextLocation(Direction.EAST, speed)
            }

            Direction.SOUTH_EAST -> {
                row = this.row.nextLocation(Direction.SOUTH, speed)
                col = this.col.nextLocation(Direction.EAST, speed)
            }

            Direction.SOUTH -> {
                row = this.row.nextLocation(Direction.SOUTH, speed)
                col = this.col
            }

            Direction.SOUTH_WEST -> {
                row = this.row.nextLocation(Direction.SOUTH, speed)
                col = this.col.nextLocation(Direction.WEST, speed)
            }

            Direction.WEST -> {
                row = this.row
                col = this.col.nextLocation(Direction.WEST, speed)
            }

            Direction.NORTH_WEST -> {
                row = this.row.nextLocation(Direction.NORTH, speed)
                col = this.col.nextLocation(Direction.WEST, speed)
            }
        }
        return Location(row, col)
    }

    fun moveFireBall() {
        val grid2 = Array(size.toInt()) { Array(size.toInt()) { ArrayList<FireBall>() } }

        for(row in 0 until size) {
            for(col in 0 until size) {
                val list = grid[row][col]
                val currentLocation = Location(row.toByte(), col.toByte())
                for(fireball in list) {
                    val nextLocation = currentLocation.nextLocation(fireball.direction, fireball.speed)
                    grid2[nextLocation.row.toInt()][nextLocation.col.toInt()].add(fireball)
                }
            }
        }

        for(row in 0 until size) {
            for(col in 0 until size) {
                val list = grid2[row][col]
                if(list.isEmpty()) {
                    grid[row][col].clear()
                    continue
                }
                if(list.size == 1) {
                    grid[row][col] = list
                    continue
                }

                grid[row][col].clear()

                val mass = list.stream().mapToInt { it.mass }.sum() / 5
                val speed = list.stream().mapToInt { it.speed }.sum() / list.size

                val con1 = list.stream().allMatch { it.direction.ordinal % 2 == 0 }
                val con2 = list.stream().allMatch { it.direction.ordinal % 2 == 1 }

                if(mass > 0) {
                    if (con1 || con2) {
                        for (direction in 0..6 step 2) {
                            grid[row][col].add(FireBall(mass, speed, direction.toDirection()))
                        }
                    } else {
                        for(direction in 1..7 step 2) {
                            grid[row][col].add(FireBall(mass, speed, direction.toDirection()))
                        }
                    }
                }
            }
        }
    }

    fun sumOfMass() : Int {
        var result = 0

        for(row in 0 until size) {
            for(col in 0 until size) {
                for(fireball in grid[row][col]) {
                    result += fireball.mass
                }
            }
        }
        return result
    }
}

fun solve(size : Short, count : Short, board : Array<Array<ArrayList<FireBall>>>) : Int {
    val grid = Grid(size, count, board)

    for(i in 1..count) {
        grid.moveFireBall()
    }

    return grid.sumOfMass()
}

fun main() {
    val (n, m, k) = readln().split(Regex("\\s+")).map { it.toShort() }
    val board = Array(n.toInt()) { Array(n.toInt()) { ArrayList<FireBall>() } }
    for(i in 0 until m) {
        val (r, c, mass, speed, direction) = readln().split(Regex("\\s+")).map { it.toInt() }
        board[r - 1][c - 1].add(FireBall(mass, speed, direction.toDirection()))
    }

    println(solve(n, k, board))
}
