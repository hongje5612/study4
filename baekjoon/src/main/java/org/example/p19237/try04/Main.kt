package org.example.p19237.try04

class Location(val row : Byte, val col : Byte) {
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    override fun toString(): String {
        return "Location($row, $col)"
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }

fun Int.toDirection() : Direction =
    when(this) {
        1 -> Direction.UP
        2 -> Direction.DOWN
        3 -> Direction.LEFT
        4 -> Direction.RIGHT
        else -> throw IllegalArgumentException("Direction must be 1~4. (It is $this)")
    }

fun Location.nextLocation(direction: Direction) : Location =
    when(direction) {
        Direction.UP -> Location(row - 1, col)
        Direction.DOWN -> Location(row + 1, col)
        Direction.LEFT -> Location(row, col - 1)
        Direction.RIGHT -> Location(row, col + 1)
    }

class Shark(val number : Short, var direction: Direction, val priority : HashMap<Direction, Array<Direction>>)

class Smell(val number : Short, var count : Short) {
    fun ifItSmells() = count > 0
    fun decrease() { count-- }
}

class Cell(var shark : Shark? = null, var smell : Smell? = null)

class Space(val size : Short, val count : Short, val locationOfSharks : HashMap<Short, Location>, sharks : HashMap<Short, Shark>) {
    //Space(공간) 내의 현제 상어와 냄새 정보를 담고 있다.
    private val board : Array<Array<Cell>> = Array(size.toInt()) { Array(size.toInt()) { Cell() } }

    // 이동을 했을 경우 이동한 상어들 중 크기가 가장 작은 상어의 정보를 담고 있다.
    private val board2 : Array<Array<Shark?>> = Array(size.toInt()) { Array(size.toInt()) { null } }

    init {
        for((number, location) in locationOfSharks) {
            val shark = sharks[number] ?: throw IllegalStateException("Shark $number is not exist")
            board[location.row.toInt()][location.col.toInt()].shark = shark
            board[location.row.toInt()][location.col.toInt()].smell = Smell(shark.number, count)
        }
    }

    private fun takeShark(number : Short) : Shark {
        val location = locationOfSharks[number] ?: throw IllegalStateException("Shark $number is not exist")
        locationOfSharks.remove(number)
        val shark = board[location.row.toInt()][location.col.toInt()].shark ?: throw IllegalStateException("Shark is null")
        board[location.row.toInt()][location.col.toInt()].shark = null
        return shark
    }

    private fun placeShark(row : Byte, col : Byte, shark : Shark) {
        val shark2 = board2[row.toInt()][col.toInt()]
        if(shark2 != null) {
            if(shark.number < shark2.number) board2[row.toInt()][col.toInt()] = shark
        } else {
            board2[row.toInt()][col.toInt()] = shark
        }
    }

    private fun isValidLocation(row : Byte, col : Byte) : Boolean {
        return (row in 0 until size) && (col in 0 until size)
    }

    private fun isValidLocation(location : Location) : Boolean {
        return isValidLocation(location.row, location.col)
    }

    private fun moveShark(number : Short) {
        val location = locationOfSharks[number] ?: throw IllegalStateException("Shark $number is not exist in locationOfSharks")
        val shark = takeShark(number)
        val directions = shark.priority[shark.direction]!!

        for(direction in directions) {
            val next = location.nextLocation(direction)
            if(isValidLocation(next )) {
                val cell = board[next.row.toInt()][next.col.toInt()]
                if (cell.shark == null && cell.smell == null) {
                    shark.direction = direction
                    placeShark(next.row, next.col, shark)
                    return
                }
            }
        }

        for(direction in directions) {
            val next = location.nextLocation(direction)
            if(isValidLocation(next)) {
                val cell = board[next.row.toInt()][next.col.toInt()]
                if (cell.smell != null && cell.smell!!.number == shark.number) {
                    shark.direction = direction
                    placeShark(next.row, next.col, shark)
                    return
                }
            }
        }
    }

    fun decreaseSmellCount() {
        for(row in 0 until size) {
            for(col in 0 until size) {
                val cell = board[row][col]
                if(cell.smell != null) {
                    if(cell.smell!!.ifItSmells()) {
                        cell.smell!!.decrease()
                        if(!(cell.smell!!.ifItSmells())) cell.smell = null
                    }
                }
            }
        }
    }

    fun updateBoard() {
        for(row in 0 until size) {
            for(col in 0 until size) {
                val shark = board2[row][col]
                if(shark != null) {
                    board[row][col].shark = shark
                    board[row][col].smell = Smell(shark.number, count)
                    locationOfSharks[shark.number] = Location(row, col)
                    board2[row][col] = null
                }
            }
        }
    }

    fun moveSharks() {
        val numbers = ArrayList<Short>()
        for((number, location) in locationOfSharks) numbers.add(number)
        for(number in numbers) moveShark(number)
    }

    fun isThereOnlyOneSharkLeft() : Boolean {
        return locationOfSharks.size == 1
    }
}

fun solve(size : Short, count : Short, locationOfSharks : HashMap<Short, Location>, sharks : HashMap<Short, Shark>) : Int {
    val space = Space(size, count, locationOfSharks, sharks)
    if(space.isThereOnlyOneSharkLeft()) return 0

    for(round in 1..1000) {
        space.moveSharks()
        space.decreaseSmellCount()
        space.updateBoard()
        if(space.isThereOnlyOneSharkLeft()) return round
    }
    return -1
}

fun main() {
    val (n, m, k) = readln().split(Regex("\\s+")).map { it.toShort() }
    val locationOfSharks = HashMap<Short, Location>()
    val ZERO : Short = 0
    for(row in 0 until n) {
        val line = readln().split(Regex("\\s+")).map { it.toShort() }
        for((index, value) in line.withIndex()) {
            if(value != ZERO) locationOfSharks[value] = Location(row, index)
        }
    }

    val directions = readln().split(Regex("\\s+")).map { it.toInt().toDirection() }

    val sharks = HashMap<Short, Shark>()
    for(number in 1..m) {
        val priority = HashMap<Direction, Array<Direction>>()
        for(direction in 1..4) {
            val array = readln().split(Regex("\\s+")).map { it.toInt().toDirection() }.toTypedArray()
            priority[direction.toDirection()] = array
        }
        val shark = Shark(number.toShort(), directions[number - 1], priority)
        sharks[number.toShort()] = shark
    }

    println(solve(n, k, locationOfSharks, sharks))
}