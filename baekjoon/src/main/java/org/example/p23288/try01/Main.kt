package org.example.p23288.try01

/**
 * 출처 : https://www.acmicpc.net/problem/23288
 * 제목 : 주사위 굴리기 2
 *
 * @author
 *  조홍제
 */

/**
 * 지도 내에서의 방향을 나내내는 클래스
 */
enum class Direction {
    NORTH, SOUTH, EAST, WEST;

    /**
     * @return
     *  현제 방향의 정 반대 방향을 반환합니다.
     */
    fun opposite() : Direction =
        when(this) {
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
            Direction.EAST -> Direction.WEST
            Direction.WEST -> Direction.EAST
        }

    fun rotate90DegreesClockwise() : Direction =
        when(this) {
            Direction.NORTH -> Direction.EAST
            Direction.EAST -> Direction.SOUTH
            Direction.SOUTH -> Direction.WEST
            Direction.WEST -> Direction.NORTH
        }

    fun rotate90DegreesCounterclockwise() : Direction =
        when(this) {
            Direction.NORTH -> Direction.WEST
            Direction.WEST -> Direction.SOUTH
            Direction.SOUTH -> Direction.EAST
            Direction.EAST -> Direction.NORTH
        }
}

/**
 * 지도 내에서 위치를 나타내는 클래스
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    fun upLocation() : Location = Location(row - 1, col)

    fun downLocation() : Location = Location(row + 1, col)

    fun leftLocation() : Location = Location(row, col - 1)

    fun rightLocation() : Location = Location(row, col + 1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (row != other.row) return false
        if (col != other.col) return false

        return true
    }

    override fun hashCode(): Int {
        var result : Int = row.toInt()
        result = 31 * result + col
        return result
    }
}

/**
 * 주사위 클래스
 *  주사위는 지도 위에 윗 면이 1이고, 동쪽을 바라보는 방향이 3인 상태로 놓여져 있으며,
 *  놓여져 있는 곳의 좌표는 (0, 0) 이다
 */
class Dice {
    private val sides1 = byteArrayOf(4, 1, 3)       // 동서 방향, 4 가 서쪽 3 이 동쪽
    private val sides2 = byteArrayOf(2, 1, 5)       // 남북 방향, 2 가 북쪽 5 가 남쪽
    private var bottomSide : Byte = 6

    /**
     * 주사위를 주어지는 방향으로 한 칸 굴린다.
     */
    fun rollTheDice(direction : Direction) {
        var t : Byte

        when(direction) {
            Direction.NORTH -> {
                t = bottomSide
                bottomSide = sides2[0]
                sides2[0] = sides2[1]
                sides2[1] = sides2[2]
                sides2[2] = t

                sides1[1] = sides2[1]
            }

            Direction.SOUTH -> {
                t = bottomSide
                bottomSide = sides2[2]
                sides2[2] = sides2[1]
                sides2[1] = sides2[0]
                sides2[0] = t

                sides1[1] = sides2[1]
            }

            Direction.EAST -> {
                t = bottomSide
                bottomSide = sides1[2]
                sides1[2] = sides1[1]
                sides1[1] = sides1[0]
                sides1[0] = t

                sides2[1] = sides1[1]
            }

            Direction.WEST -> {
                t = bottomSide
                bottomSide = sides1[0]
                sides1[0] = sides1[1]
                sides1[1] = sides1[2]
                sides1[2] = t

                sides2[1] = sides1[1]
            }
        }
    }

    /**
     * 주사위 아랫 면에 적힌 숫자를 반환합니다.
     */
    fun numberOnTheBottomOfTheDice() = bottomSide
}

/**
 * 문제를 해결하는 클래스
 *
 * @param
 *  rowSize : map 의 행의 크기
 *  colSize : map 의 열의 크기
 *  count : 주사위가 이동하는 횟 수
 */
class Solution(val rowSize : Byte, val colSize : Byte, val count : Short, val map : Array<ByteArray>) {

    companion object {
        val DX = intArrayOf(0, 0, -1, 1)    // 상하좌우
        val DY = intArrayOf(-1, 1, 0, 0)    // 상하좌우
    }

    private val dice = Dice()
    private var directionOfDice = Direction.EAST    // 주사위의 최초 방향은 동쪽이다. 무조건 동쪽으로 한칸 이동한 후 부터 진행한다.
    private var locationOfDice = Location(0, 0)

    private val answer : Int  // 문제의 해답을 가지는 변수

    private fun Location.isValid() : Boolean = (row in 0 until rowSize) && (col in 0 until colSize)

    private fun isValidLocation(row : Byte, col : Byte) : Boolean = (row in 0 until rowSize) && (col in 0 until colSize)

    private fun isValidLocation(location : Location) : Boolean = location.isValid()

    class LocationDirection(val location : Location, val direction : Direction)

    /**
     * 주사위가 direction 방향으로 한 번 굴렀을 때 다음 위치와 방향를 반환한다.
     * 지도 밖으로 나가는 경우 반대 방향으로 움직인다.
     */
    private fun Location.nextLocation(direction : Direction) : LocationDirection {
        when(direction) {
            Direction.NORTH -> {
                val upLocation = this.upLocation()
                return if(upLocation.isValid()) LocationDirection(upLocation, direction)
                else LocationDirection(downLocation(), direction.opposite())
            }

            Direction.SOUTH -> {
                val downLocation = this.downLocation()
                return if(downLocation.isValid()) LocationDirection(downLocation, direction)
                else LocationDirection(upLocation(), direction.opposite())
            }

            Direction.EAST -> {
                val rightLocation = this.rightLocation()
                return if(rightLocation.isValid()) LocationDirection(rightLocation, direction)
                else LocationDirection(leftLocation(), direction.opposite())
            }

            Direction.WEST -> {
                val leftLocation = this.leftLocation()
                return if(leftLocation.isValid()) LocationDirection(leftLocation, direction)
                else LocationDirection(rightLocation(), direction.opposite())
            }
        }
    }

    /**
     * 주사위를 한 번 굴린다.
     */
    private fun rollDice() {
        val locationDirection = locationOfDice.nextLocation(directionOfDice)
        locationOfDice = locationDirection.location
        directionOfDice = locationDirection.direction
        dice.rollTheDice(directionOfDice)
    }

    /**
     * @param
     *  row : 행의 위치
     *  col : 열의 위치
     *
     * @return
     *  bfs(너비 우선 탐색) 알고리즘을 이용하여 현제 위치의 점수를 반환합니다.
     */
    private fun score(row : Byte, col : Byte) : Int  {
        val visited = Array(rowSize.toInt()) { BooleanArray(colSize.toInt()) { false } }    // 방문한 적이 없다.
        val inQueue = Array(rowSize.toInt()) { BooleanArray(colSize.toInt()) { false } }    // queue  에 아무것도 존재하지 않는다.

        val queue = ArrayDeque<Location>()
        queue.addLast(Location(row, col))
        inQueue[row.toInt()][col.toInt()] = true
        val number = map[row.toInt()][col.toInt()]
        var count = 0

        while(queue.isNotEmpty()) {
            val loc = queue.removeFirst()
            count++
            inQueue[loc.row.toInt()][loc.col.toInt()] = false
            visited[loc.row.toInt()][loc.col.toInt()] = true

            for(i in 0 until DX.size) {
                val r = loc.row.toInt() + DY[i]
                val c = loc.col.toInt() + DX[i]

                if(isValidLocation(r.toByte(), c.toByte())) {
                    if(number == map[r][c]) {
                        if(!visited[r][c] && !inQueue[r][c]) {
                            queue.addLast(Location(r.toByte(), c.toByte()))
                            inQueue[r][c] = true
                        }
                    }
                }
            }
        }
        return number * count
    }

    private fun simulate() : Int {
        var score : Int = 0

        for(i in 0 until count) {
            rollDice()
            score += score(locationOfDice.row, locationOfDice.col)

            val numberOnMap = map[locationOfDice.row.toInt()][locationOfDice.col.toInt()]
            val bottomNumberOfDice = dice.numberOnTheBottomOfTheDice()
            if(bottomNumberOfDice > numberOnMap) directionOfDice = directionOfDice.rotate90DegreesClockwise()
            else if(bottomNumberOfDice < numberOnMap) directionOfDice = directionOfDice.rotate90DegreesCounterclockwise()
        }

        return score
    }

    init {
        answer = simulate()
    }

    fun getAnswer() : Int = answer
}

fun main() {
    val (n, m, k) = readln().split(Regex("\\s+")).map { it.toShort() }
    val map = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }

    println("${Solution(n.toByte(), m.toByte(), k, map).getAnswer()}")
}
