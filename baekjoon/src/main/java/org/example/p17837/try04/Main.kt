package org.example.p17837.try04

class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    override fun toString(): String {
        return "Location(row=$row, col=$col)"
    }
}

fun Location.oneStep(direction: Direction) : Location =
    when(direction) {
        Direction.RIGHT -> Location(this.row, this.col + 1)
        Direction.LEFT -> Location(this.row, this.col - 1)
        Direction.UP -> Location(this.row - 1, this.col)
        Direction.DOWN -> Location(this.row + 1, this.col)
    }

enum class Direction {
    RIGHT, LEFT, UP, DOWN;

    fun reverse() : Direction =
        when(this) {
            RIGHT -> LEFT
            LEFT -> RIGHT
            UP -> DOWN
            DOWN -> UP
        }
}

fun Int.toDirection() : Direction =
    when (this) {
        1 -> Direction.RIGHT
        2 -> Direction.LEFT
        3 -> Direction.UP
        4 -> Direction.DOWN
        else -> throw IllegalArgumentException("Invalid direction value: $this")
    }

enum class Color {
    WHITE, RED, BLUE
}

fun Int.toColor() : Color =
    when (this) {
        0 -> Color.WHITE
        1 -> Color.RED
        2 -> Color.BLUE
        else -> throw IllegalArgumentException("Invalid color value: $this")
    }

class Disk(val number : Byte, var direction : Direction, var underDisk : Disk? = null, var overDisk : Disk? = null) {
    var size : Byte = 1
        private set

    companion object {
        fun separate(disk : Disk, number : Byte) : Pair<Disk?, Disk?> {
            if(disk.number == number) return disk to null

            var currentDisk : Disk? = disk
            while(currentDisk != null) {
                if(currentDisk.number == number) {
                    var underDisk = currentDisk.underDisk
                    currentDisk.underDisk = null
                    underDisk?.overDisk= null

                    while(underDisk != null) {
                        underDisk.size = (underDisk.size - currentDisk.size).toByte()
                        underDisk = underDisk.underDisk
                    }

                    return currentDisk to disk
                }
                currentDisk = currentDisk.overDisk
            }

            return null to disk
        }
    }

    fun top() : Disk {
        var currentDisk : Disk? = this
        if(currentDisk!!.overDisk == null) return currentDisk
        var previousDisk = currentDisk
        currentDisk = currentDisk.overDisk

        while(currentDisk != null) {
            previousDisk = currentDisk
            currentDisk = currentDisk.overDisk
        }
        return previousDisk!!
    }

    fun stackOver(disk : Disk) {
        val top = top()
        top.overDisk = disk
        disk.underDisk = top

        var currentDisk : Disk? = top
        while(currentDisk != null) {
            currentDisk.size = (currentDisk.size + disk.size).toByte()
            currentDisk = currentDisk.underDisk
        }
    }

    fun reverse() : Disk {
        val top = top()
        val reverseDisk = Disk(top.number, top.direction)

        var currentDisk = top.underDisk
        while(currentDisk != null) {
            reverseDisk.stackOver(Disk(currentDisk.number, currentDisk.direction))
            currentDisk = currentDisk.underDisk
        }
        return reverseDisk
    }
}

class Cell(val color : Color, var disk : Disk? = null)

class Board(val sizeOfBoard : Byte, val sizeOfChessman : Byte, colorInformation : Array<String>, chessmanInformation : Array<String>) {
    private val board = Array(sizeOfBoard.toInt()) { row ->
        val ss = colorInformation[row].split(Regex("\\s+"))
        Array(sizeOfBoard.toInt()) { col ->
            val color = ss[col].toInt().toColor()
            Cell(color)
        }
    }
    private val locationOfDisk = HashMap<Byte, Location>()

    init {
        for((index, str) in chessmanInformation.withIndex()) {
            val ss = str.split(Regex("\\s+"))
            val row = ss[0].toInt() - 1
            val col = ss[1].toInt() - 1
            val direction = ss[2].toInt().toDirection()
            val disk = Disk((index + 1).toByte(), direction)
            board[row][col].disk = disk
            locationOfDisk[disk.number] = Location(row, col)
        }
    }

    private fun insertLocationInfo(row : Byte, col : Byte, disk : Disk?) {
        var currentDisk : Disk? = disk
        while(currentDisk != null) {
            locationOfDisk[currentDisk.number] = Location(row, col)
            currentDisk = currentDisk.overDisk
        }
    }

    private fun placeDisk(row : Byte, col : Byte, disk : Disk?) : Boolean {
        if(disk == null) return false

        val anotherDisk = takeDisk(row, col)
        if(anotherDisk != null) {
            anotherDisk.stackOver(disk)
            insertLocationInfo(row, col, anotherDisk)
            board[row.toInt()][col.toInt()].disk = anotherDisk
            return anotherDisk.size >= 4
        } else {
            board[row.toInt()][col.toInt()].disk = disk
            insertLocationInfo(row, col, disk)
            return false
        }
    }

    private fun takeDisk(row : Byte, col : Byte) : Disk? {
        val disk = board[row.toInt()][col.toInt()].disk
        board[row.toInt()][col.toInt()].disk = null

        var currentDisk : Disk? = disk
        while(currentDisk != null) {
            locationOfDisk.remove(currentDisk.number)
            currentDisk = currentDisk.overDisk
        }
        return disk
    }

    private fun isValid(row : Byte, col : Byte) : Boolean = (row in 0 until sizeOfBoard) && (col in 0 until sizeOfBoard)
    private fun isValid(location : Location) = isValid(location.row, location.col)

    fun moveDisk(number : Byte) : Boolean {
        fun doThisIfItsWhite(nextLocation : Location, target : Disk) : Boolean {
            return placeDisk(nextLocation.row, nextLocation.col, target)
        }

        fun doThisIfItsRed(nextLocation : Location, target : Disk) : Boolean {
            val reverse = target.reverse()
            return placeDisk(nextLocation.row, nextLocation.col, reverse)
        }

        fun doThisIfItsBlue(currentLocation : Location, target : Disk) : Boolean {
            target.direction = target.direction.reverse()
            val nextLocation = currentLocation.oneStep(target.direction)
            if(isValid(nextLocation)) {
                val color = board[nextLocation.row.toInt()][nextLocation.col.toInt()].color
                when(color) {
                    Color.WHITE -> {
                        return doThisIfItsWhite(nextLocation, target)
                    }
                    Color.RED -> {
                        return doThisIfItsRed(nextLocation, target)
                    }
                    Color.BLUE -> {
                        return placeDisk(currentLocation.row, currentLocation.col, target)
                    }
                }
            } else {
                return placeDisk(currentLocation.row, currentLocation.col, target)
            }
        }

        val currentLocation = locationOfDisk[number] ?: throw IllegalStateException("The Information of location not exist.")
        val disk = takeDisk(currentLocation.row, currentLocation.col) ?: throw IllegalStateException("Disk not exist in $currentLocation")
        val (target, original) = Disk.separate(disk, number)
        if(original != null) placeDisk(currentLocation.row, currentLocation.col, original)
        if(target == null) throw IllegalStateException("Disk $number is not exist")
        val nextLocation = currentLocation.oneStep(target.direction)

        if(isValid(nextLocation)) {
            val color = board[nextLocation.row.toInt()][nextLocation.col.toInt()].color
            return when(color) {
                Color.WHITE -> {
                    doThisIfItsWhite(nextLocation, target)
                }

                Color.RED -> {
                    doThisIfItsRed(nextLocation, target)
                }

                Color.BLUE -> {
                    doThisIfItsBlue(currentLocation, target)
                }
            }
        } else {
            return doThisIfItsBlue(currentLocation, target)
        }
    }
}

fun simulate(board : Board) : Int {
    for(turn in 1..1000) {
        for(number in 1..board.sizeOfChessman) {
            if(board.moveDisk(number.toByte())) return turn
        }
    }
    return -1
}

fun main() {
    val (sizeOfBoard, sizeOfChessman) = readln().split(Regex("\\s+")).map { it.toByte() }
    val colorInformation = Array(sizeOfBoard.toInt()) { readln() }
    val chessmanInformation = Array(sizeOfChessman.toInt()) { readln() }
    val board = Board(sizeOfBoard, sizeOfChessman, colorInformation, chessmanInformation)

    println(simulate(board))
}
