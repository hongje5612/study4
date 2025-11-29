package org.example.misc

enum class Direction {
    EAST, WEST, SOUTH, NORTH
}

fun left(direction: Direction) : Direction {
    when(direction) {
        Direction.EAST -> return Direction.NORTH
        Direction.WEST -> return Direction.SOUTH
        Direction.NORTH -> return Direction.WEST
        Direction.SOUTH -> return Direction.EAST
    }
}

fun right(direction: Direction) : Direction {
    when(direction) {
        Direction.SOUTH -> return Direction.WEST
        Direction.NORTH -> return Direction.EAST
        Direction.WEST -> return Direction.NORTH
        Direction.EAST -> return Direction.SOUTH
    }
}