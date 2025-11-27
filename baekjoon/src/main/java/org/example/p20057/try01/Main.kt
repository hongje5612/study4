package org.example.p20057.try01

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

/**
 * 네가지 방향 (상하좌우)를 표현합니다.
 */
enum class Direction {
    UP,     // 위쪽
    DOWN,   // 아래쪽
    LEFT,   // 왼쪽
    RIGHT   // 오른쪽
}

/**
 * @return
 * 토네이도가 소용돌이 방향으로 회전 할 때, 현제 방향에서 다른 방향으로 바뀌는데, 그 바뀌는 방향을 반환합니다.
 */
fun Direction.nextDirection() : Direction =
    when(this) {
        Direction.UP -> Direction.LEFT
        Direction.DOWN -> Direction.RIGHT
        Direction.LEFT -> Direction.DOWN
        Direction.RIGHT -> Direction.UP
    }

/**
 * 행과 열을 이용하여 2차원 위치를 표현합니다.
 *
 * @param
 * row : 행의 위치
 * col : 열의 위치
 */
class Location(val row : Short, val col : Short) {
    constructor(row : Int, col : Short) : this(row.toShort(), col)
    constructor(row : Short, col : Int) : this(row, col.toShort())
    constructor(row : Int, col : Int) : this(row.toShort(), col.toShort())

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

    override fun toString(): String {
        return "Location(row=$row, col=$col)"
    }
}

/**
 * @param
 *      direction : 방향
 *
 * @return
 *      현제 위치에서 주어지는 방향으로 한 칸 움직였을 경우의 위치를 반환합니다.
 */
fun Location.nextLocation(direction : Direction) : Location =
    when(direction) {
        Direction.UP -> Location(row - 1, col)
        Direction.DOWN -> Location(row + 1, col)
        Direction.LEFT -> Location(row, col - 1)
        Direction.RIGHT -> Location(row, col + 1)
    }

/**
 * @param
 * size : 토네이도가 지나가는 영역이 크기, 행의 크기 == 열의 크기
 */
class TornadoSimulator(val size : Short) {

    class Tornado(var location : Location, var direction : Direction)

    /**
     * 토네이도가 지나간 자리는 참, 아직 지나가지 않은 자리는 거짓
     */
    private val board = Array(size.toInt()) { Array<Boolean>(size.toInt()) { false } }

    /**
     * 현제 토네이도의 정보를 저장합니다.
     */
    private val currentTornado : Tornado

    /**
     * 토네이도가 지나가는 영역의 중심 인덱스
     */
    private val center = size / 2
    private val centerLocation = Location(center, center)

    init {
        // 현제 토네이도의 위치 마킹
        board[center][center] = true
        currentTornado = Tornado(Location(center, center), Direction.LEFT)
    }

    /**
     * @return
     * 토네이도의 현제 위치를 반환합니다.
     */
    fun currentLocation() : Location = currentTornado.location

    /**
     * @return
     * 토네이도의 현제 방향을 반환합니다.
     */
    fun currentDirection() : Direction = currentTornado.direction

    /**
     * @param
     * row : 행 번호
     * col : 열 번호
     *
     * @return
     * (행, 열) 의 위치가 size 범위 안에 있으면 참, 그렇지 않고 영역의 바꾸로 나가면 거짓
     */
    private fun isValidLocation(row : Short, col : Short) : Boolean {
        return (row in 0 until size) && (col in 0 until size)
    }

    private fun isValidLocation(location : Location) : Boolean {
        return isValidLocation(location.row, location.col)
    }

    /**
     * 토네이도를 한칸 움직입니다.
     *
     */
    fun moveTornado() {
        if(currentTornado.location == centerLocation) {
            val nextLocation = currentTornado.location.nextLocation(currentTornado.direction)
            currentTornado.location = nextLocation
            board[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()] = true
        } else {
            val row = currentTornado.location.row.toInt()
            val col = currentTornado.location.col.toInt()
            val r : Int
            var c : Int

            when(currentTornado.direction) {
                Direction.UP -> {
                    c = col - 1
                    if(isValidLocation(row.toShort(), c.toShort())) {
                        if (!board[row][c]) {
                            currentTornado.location = Location(row, c)
                            currentTornado.direction = Direction.LEFT
                            board[row][c] = true
                            return
                        }
                    } else throw IllegalStateException("지나가는 방향의 왼쪽 위치가 존재하지 않습니다.")

                    r = row - 1
                    if(isValidLocation(r.toShort(), col.toShort())) {
                        currentTornado.location = Location(r, col)
                        board[r][col] = true
                        return
                    } else throw IllegalStateException("위쪽으로 올라갈 수 없습니다.")
                }

                Direction.DOWN -> {
                    c = col + 1
                    if(isValidLocation(row.toShort(), c.toShort())) {
                        if (!board[row][c]) {
                            currentTornado.location = Location(row, c)
                            currentTornado.direction = Direction.RIGHT
                            board[row][c] = true
                            return
                        }
                    } else throw IllegalStateException("지나가는 방향의 오른쪽 위치가 존재하지 않습니다.")

                    r = row + 1
                    if(isValidLocation(r.toShort(), col.toShort())) {
                        currentTornado.location = Location(r, col)
                        board[r][col] = true
                        return
                    } else throw IllegalStateException("아래쪽으로 내려갈 수 없습니다.")
                }

                Direction.LEFT -> {
                    r = row + 1
                    if(isValidLocation(r.toShort(), col.toShort())) {
                        if (!board[r][col]) {
                            currentTornado.location = Location(r, col)
                            currentTornado.direction = Direction.DOWN
                            board[r][col] = true
                            return
                        }
                    } else throw IllegalStateException("지나가는 방향의 야래 위치가 존재하지 않습니다.")

                    c = col - 1
                    if(isValidLocation(row.toShort(), c.toShort())) {
                        currentTornado.location = Location(row, c)
                        board[row][c] = true
                        return
                    } else throw IllegalStateException("왼쪽으로 갈 수 없습니다.")
                }

                Direction.RIGHT -> {
                    r = row - 1
                    if(isValidLocation(r.toShort(), col.toShort())) {
                        if (!board[r][col]) {
                            currentTornado.location = Location(r, col)
                            currentTornado.direction = Direction.UP
                            board[r][col] = true
                            return
                        }
                    } else throw IllegalStateException("지나가는 방향의 위쪽 위치가 존재하지 않습니다.")

                    c = col + 1
                    if(isValidLocation(row.toShort(), c.toShort())) {
                        currentTornado.location = Location(row, c)
                        board[row][c] = true
                        return
                    } else throw IllegalStateException("오른쪽으로 갈 수 없습니다.")
                }
            }
        }
    }
}

/**
 * 상대적인 위치를 나타내는 클래스
 * 원점 (0, 0) x측 방향으로 dx 만큼, y 출 방향으로 dy 만큼 떨어져 있다는 것을 표현하는 클래스
 * 데카르트 좌표계를 사용한다.
 */
class Distance(val dy : Short, val dx : Short)

/**
 * 문제에서 제시하는 모래가 바람에 날라가는 비율을 표현합니다.
 */
object Rate {
    private val rate = HashMap<Direction, HashMap<Distance, Byte>>()

    init {
        /*
        왼쪽 방향으로 움직일 때의 비율들의 위치
        위치는 데카르트 좌표계로 표현한다. 그러나 차후에 이 좌표를 이용할 때는 y 값에 음수 부호를 붙혀서 사용한다.
         */
        val left = HashMap<Distance, Byte>()

        left[Distance(1, 1)] = 1
        left[Distance(-1, 1)] = 1

        left[Distance(1, 0)] = 7
        left[Distance(2, 0)] = 2
        left[Distance(-1, 0)] = 7
        left[Distance(-2, 0)] = 2

        left[Distance(1, -1)] = 10
        left[Distance(-1, -1)] = 10

        left[Distance(0, -2)] = 5

        // 계산에서는 사용되지 않고, 단지 이 위치에 a(알파) 값이 온다는 것을 표현합니다.
        left[Distance(0, -1)] = 55

        rate[Direction.LEFT] = left

        /*
        아래쪽, 오른쪽, 위쪽으로 움직일 경우의 비율 분포
         */
        val down = HashMap<Distance, Byte>()
        val right = HashMap<Distance, Byte>()
        val up = HashMap<Distance, Byte>()

        for((distance, value) in left) {
            val downDistance = distance.rotate(90)
            val rightDistance = distance.rotate(180)
            val upDistance = distance.rotate(270)

            down[downDistance] = value
            right[rightDistance] = value
            up[upDistance] = value
        }

        rate[Direction.DOWN] = down
        rate[Direction.RIGHT] = right
        rate[Direction.UP] = up
    }

    // 각도 변환
    // Degree 값을 Radian 값으로 변환합니다.
    private fun Int.toRadian() = this * PI / 180

    // 위치를 주어진 각도의 값으로 회전한 결과를 반환합니다.
    private fun Distance.rotate(degree : Int) : Distance {
        val x : Double = dx.toDouble()
        val y : Double = dy.toDouble()
        val cosine = cos(degree.toRadian().toDouble())
        val sine = sin(degree.toRadian().toDouble())

        val x1 = x * cosine - y * sine
        val y1 = x * sine + y * cosine

        return Distance(round(y1).toInt().toShort(), round(x1).toInt().toShort())
    }

    fun show() {
        for((direction, map) in rate) {
            println(direction.name)
            for((location, value) in map) {
                println("$location = $value")
            }
        }
    }

    fun getRate(direction : Direction) : HashMap<Distance, Byte> = rate[direction] ?: throw java.lang.IllegalStateException("주어진 방향의 비율 정보가 없습니다.")
}

class Solution(val size : Short, val board : Array<IntArray>) {
    private val tornadoSimulator = TornadoSimulator(size)

    private fun isValidLocation(row : Int, col : Int) : Boolean = (row in 0 until size) && (col in 0 until size)

    private fun oneStep() : Long {
        tornadoSimulator.moveTornado()
        val nextLocation = tornadoSimulator.currentLocation()
        val direction = tornadoSimulator.currentDirection()
        val currentAmount = board[nextLocation.row.toInt()][nextLocation.col.toInt()]
        var answer : Long = 0L      // 밖으로 나간 양
        var total : Int = 0       // 움직인 모래의 양

        val map = Rate.getRate(direction)

        var row : Int
        var col : Int
        var distanceOfRate55 = Distance(0, 0)      // 비율이 55 인 위치
        for((distance, rate) in map) {
            if(rate == 55.toByte()) {
                distanceOfRate55 = distance
                continue
            }
            row = nextLocation.row - distance.dy
            col = nextLocation.col + distance.dx
            val amount = (currentAmount * (rate.toDouble() / 100.toDouble())).toInt()
            total += amount

            if(isValidLocation(row, col)) {
                board[row][col] += amount
            } else answer += amount
        }

        row = nextLocation.row - distanceOfRate55.dy
        col = nextLocation.col + distanceOfRate55.dx
        if(isValidLocation(row, col)) {
            board[row][col]  += (currentAmount - total)
        } else answer += (currentAmount - total)

        board[nextLocation.row.toInt()][nextLocation.col.toInt()] = 0

        return answer
    }

    fun simulate() : Long {
        var answer = 0L     // 밖으로 나간 양

        while(true) {
            try {
                answer += oneStep()
            } catch (e : Exception) {
                break
            }
        }
        return answer
    }
}

fun main() {
    val size : Short = readln().toShort()
    val board = Array<IntArray>(size.toInt()) { readln().split(Regex("\\s+")).map { it.toInt()}.toIntArray() }

    println("${Solution(size, board).simulate()}")
}
