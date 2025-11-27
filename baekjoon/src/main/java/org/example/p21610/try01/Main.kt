package org.example.p21610.try01

/*
    출처 : https://www.acmicpc.net/problem/21610
    제목 : 마법사 상어와 비바라기
 */

import kotlin.math.abs

/**
 * 문제에서 제시하는 8가지 방향
 */
enum class Direction {
    WEST,
    NORTH_WEST,
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST
}

/**
 * 정수를 방향으로 변환합니다.
 */
fun Int.toDirection() : Direction =
    when(this) {
        1 -> Direction.WEST
        2 -> Direction.NORTH_WEST
        3 -> Direction.NORTH
        4 -> Direction.NORTH_EAST
        5 -> Direction.EAST
        6 -> Direction.SOUTH_EAST
        7 -> Direction.SOUTH
        8 -> Direction.SOUTH_WEST
        else -> throw IllegalArgumentException("방향을 나타내는 숫자는 1~8까지 여야 합니다.")
    }

/**
 * 문제에서 제시하는 NxN 격자 안의 위치를 나타내기 위한 클래스
 * 좌 상단은 (0, 0)
 * 우 하단은 (N - 1, N - 1)
 *
 * 문제에서는
 * 좌 상단을 (1, 1)
 * 우 하단을 (N, N) 으로 문제를 출제했기 때문에
 * 프로그램 내부에서 보정 작업을 해 주어야 합니다.
 *
 * 차후에 프로그램 내부에
 *  val clouds = HashSet<Location>()
 *  if(clouds.contains(Location(row, col))) ...
 * 이런 코드를 넣어, 이전 구름의 위치인지 아닌지를 판별할 건데,
 * 이때 contains 메서드 때문에 equals과 hashCode를 재정의 하였습니다.
 *
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {

    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

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
 * 문제에서 제시하는 이동 정보를 나타내는 클래스
 *
 * @param
 *  direction : 방향
 *  distance : 이동 거리
 */
class Order(val direction : Direction, val distance : Byte)

/**
 * 격자를 나타내는 클래스
 *
 * @param
 *  size    : 격자판의 크기
 *  amount  : 현제 물의 양을 가지고 있는 2차원 배열
 *  orders  : 이동 정보를 저장하고 있는 리스트
 */
class Grid(val size : Byte, val amount : Array<ShortArray>, val orders : List<Order>) {

    companion object {
        val DX = arrayOf(-1, 1, -1, 1)  // NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
        val DY = arrayOf(-1, -1, 1, 1)  // NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
    }

    /*
    현제의 구름 정보를 저장하는 변수
     */
    private var clouds = HashSet<Location>()

    init {
        // 최초로 생기는 구름 정보
        clouds.add(Location(size - 1, 0))
        clouds.add(Location(size - 1, 1))
        clouds.add(Location(size - 2, 0))
        clouds.add(Location(size - 2, 1))

        simulate()
    }

    /**
     * 현제의 구름을 이동 정보에 따라서 이동시킨다.
     *
     * @param
     *  order : 이동 정보
     */
    private fun moveCloud(order : Order) {
        /**
         * @param
         *  direction : 주어지는 방향으로
         *  distance : 이동하는 거리
         *
         *  @return
         *  this(현제 위치)에서 주어지는 방향으로 distance 거리 만큼 이동했을 경우의 위치
         */
        fun Location.nextLocation(direction : Direction, distance : Byte) : Location {
            /**
             * <---------------------------------------------------------------->
             *  1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0
             *                                ^
             *                             현제 위치(if this is zero)
             *
             *               <------- if direction is false
             *                        if direction is true  -------->
             *
             * @return
             *  this(현제 위치) 에서 direction 방향으로 distance 만큼의 거리를 이동했을 경우의 위치 값을 반환합니다.
             */
            fun Byte.nextLocation(direction: Boolean, distance: Byte): Byte {
                when(direction) {
                    // 좌료가 증가하는 방향으로 움직일 때
                    true -> {
                        return ((this + distance) % size).toByte()
                    }

                    // 좌표가 감소하는 방향으로 움직일 때
                    false -> {
                        val location = this - distance
                        if(location >= 0) return location.toByte()
                        val rem = abs(location) % size
                        return if(rem != 0) (size - rem).toByte() else 0
                    }
                }
            }

            var r : Byte
            var c : Byte

            when(direction) {
                Direction.WEST -> {
                    r = row
                    c = col.nextLocation(false, distance)
                }

                Direction.NORTH_WEST -> {
                    r = row.nextLocation(false, distance)
                    c = col.nextLocation(false, distance)
                }

                Direction.NORTH -> {
                    r = row.nextLocation(false, distance)
                    c = col
                }

                Direction.NORTH_EAST -> {
                    r = row.nextLocation(false, distance)
                    c = col.nextLocation(true, distance)
                }

                Direction.EAST -> {
                    r = row
                    c = col.nextLocation(true, distance)
                }

                Direction.SOUTH_EAST -> {
                    r = row.nextLocation(true, distance)
                    c = col.nextLocation(true, distance)
                }

                Direction.SOUTH -> {
                    r = row.nextLocation(true, distance)
                    c = col
                }

                Direction.SOUTH_WEST -> {
                    r = row.nextLocation(true, distance)
                    c = col.nextLocation(false, distance)
                }
            }

            return Location(r, c)
        }

        val nextClouds = HashSet<Location>()
        for(location in clouds) {
            nextClouds.add(location.nextLocation(order.direction, order.distance))
        }
        clouds = nextClouds
    }

    /**
     * 구름에서 비가 내려 구름이 있는 칸의 바구니에 저장된 물의 양이 1 증가한다
     *
     */
    private fun rainFallsFromACloud() {
        for(location in clouds) {
            amount[location.row.toInt()][location.col.toInt()]++
        }
    }

    private fun isValidLocation(row : Byte, col : Byte) : Boolean = (row in 0 until size) && (col in 0 until size)

    // private fun isValidLocation(location : Location) : Boolean = isValidLocation(location.row, location.col)

    /**
     * 물복사버그 마법을 시전합니다.
     */
    private fun castTheWaterCopyBugSpell() {
        /**
         * @param
         *  location : 위치
         *
         * @return
         *  주어진 위치에서 물이 있는 대각선 방향의 개수를 반환한다.
         */
        fun numberOfDirectionWater(location : Location) : Short {
            var count : Short = 0

            for(index in 0 until DX.size) {
                val r = location.row + DY[index]
                val c = location.col + DX[index]

                if(isValidLocation(r.toByte(), c.toByte()) && amount[r][c] > 0) count++
            }
            return count
        }

        for(location in clouds) {
            val t = numberOfDirectionWater(location)
            amount[location.row.toInt()][location.col.toInt()] = (amount[location.row.toInt()][location.col.toInt()] + t).toShort()
        }
    }

    /**
     * 바구니에 저장된 물의 양이 2 이상인 모든 칸에 구름이 생기고, 물의 양이 2 줄어든다. 이때 구름이 생기는 칸은 3에서 구름이 사라진 칸이 아니어야 한다.
     */
    private fun cloudsForm() {
        val nextClouds = HashSet<Location>()

        for(row in 0 until size) {
            for(col in 0 until size) {
                if(clouds.contains(Location(row.toByte(), col.toByte()))) continue

                if(amount[row][col] >= 2) {
                    nextClouds.add(Location(row.toByte(), col.toByte()))
                    --amount[row][col]
                    --amount[row][col]
                }
            }
        }

        clouds = nextClouds
    }

    /**
     * 비바라기 마법을 시전합니다.
     *
     * @param
     *  order : 이동 정보
     */
    private fun castThrRainSpell(order : Order) {
        moveCloud(order)
        rainFallsFromACloud()
        castTheWaterCopyBugSpell()
        cloudsForm()
    }

    /**
     * 시뮬레이션
     * 문제에서 주어지는 시뮤레이션을 수행합니다.
     */
    private fun simulate() {
        for(order in orders) {
            castThrRainSpell(order)
        }
    }

    /**
     * @return
     *  바구니에 담긴 물 양의 합을 반환합니다.
     */
    fun sumOfWater() : Int {
        var result = 0

        for(row in 0 until size) {
            for(col in 0 until size) {
                result += amount[row][col]
            }
        }

        return result
    }
}

fun main() {
    val (n, m) = readln().split(Regex("\\s+")).map { it.toByte() }
    val amount = Array(n.toInt()) { readln().split(Regex("\\s+")).map { it.toShort() }.toShortArray() }
    val orders = ArrayList<Order>()
    for(i in 1..m) {
        val (d, s) = readln().split(Regex("\\s+")).map { it.toInt() }
        orders.add(Order(d.toDirection(), s.toByte()))
    }

    println("${Grid(n, amount, orders).sumOfWater()}")
}
