package org.example.p23289

import java.util.Objects
import kotlin.math.abs
import kotlin.reflect.KFunction1

/**
 * 출처 : https://www.acmicpc.net/problem/23289
 *
 * @author
 *  조홍제 : https://blog.naver.com/hjj5612
 *
 */

/**
 * 벽의 정보를 저장하는 클래스
 *
 * @param
 *  left    : 왼쪽에 벽이 있으면 참
 *  right   : 오른쪽에 벽이 있으면 참
 *  top     : 윗쪽에 벽이 있으면 참
 *  bottom  : 아랫쪽에 벽이 있으면 참
 */
class Wall(private var left : Boolean = false,
           private var right : Boolean = false,
           private var top : Boolean = false,
           private var bottom : Boolean = false)
{
    fun isThereNoWallOnTheLeft(): Boolean = !left
    fun isThereNoWallOnTheRight(): Boolean = !right
    fun isThereNoWallOnTheTop(): Boolean = !top
    fun isThereNoWallOnTheBottom(): Boolean = !bottom

    fun buildAWallOnTheLeft() { left = true }
    fun buildAWallOnTheRight() { right = true }
    fun buildAWallOnTheTop() { top = true }
    fun buildAWallOnTheBottom() { bottom = true }
}

/**
 * R x C 크기의 집에서 1 x 1 크기의 한 칸을 표현하는 클래스, 격자판의 한 칸
 *
 * @param
 *  temperature     : 온도
 *  wall            : 벽의 정보
 */
class Cell(var temperature : Short, val wall : Wall = Wall())

/**
 * 바람이 부는 방향을 나타내는 클래스
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * R x C 의 격자판 위에서 위치는 나타내는 클래스
 *
 * @param
 *  row     : 행의 위치
 *  col     : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())

    fun north() = Location(row - 1, col)
    fun northEast() = Location(row - 1, col + 1)
    fun east() = Location(row, col + 1)
    fun southEast() = Location(row + 1, col + 1)
    fun south() = Location(row + 1, col)
    fun southWest() = Location(row + 1, col - 1)
    fun west() = Location(row, col - 1)
    fun northWest() = Location(row - 1, col - 1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (row != other.row) return false
        if (col != other.col) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(row, col)
    }
}

/**
 * 바람을 표현하는 클래스
 * 바람은 격자의 한 칸의 온도를 주어진 값으로 상승시킨다.
 *
 * @param
 *  location        : 바람의 현제 위치
 *  temperature     : 온도 (1~5)
 *  direction       : 방향
 */
class Wind(val location : Location, val temperature : Short, val direction : Direction) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Wind

        if (temperature != other.temperature) return false
        if (location != other.location) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(temperature, location, direction)
    }
}

/**
 * 온풍기를 나타내는 클래스
 *
 * @param
 *  location            : 온풍기의 위치
 *  directionOfWind     : 바람의 방향
 */
class Heater(val location : Location, val directionOfWind : Direction)

/**
 * 문제를 해결하는 클래스
 *
 * @param
 *  rowSize             : 격자판의 행의 크기
 *  colSize             : 격자판의 열의 크기
 *  k                   : 조사해야하는 셀의 온도가 k도 이상이면 종료한다.
 *  grid                : 격자판의 정보가 주어진다. (온풍기의 위치, 조사해야하는 칸의 위치)
 *                          0: 빈 칸
 *                          1: 방향이 오른쪽인 온풍기가 있음
 *                          2: 방향이 왼쪽인 온풍기가 있음
 *                          3: 방향이 위인 온풍기가 있음
 *                          4: 방향이 아래인 온풍기가 있음
 *                          5: 온도를 조사해야 하는 칸
 *  informationOfWalls  : 벽의 정보, informationOfWalls[0], informationOfWalls[1], ... 은 항상 세 개의 숫자가 제공된다.
 *                      : 다음 W개의 줄에는 벽의 정보가 주어지며, 이 정보는 세 정수 x, y, t로 이루어져 있다.
 *                      : t가 0인 경우 (x, y)와 (x-1, y) 사이에 벽이 있는 것이고, 1인 경우에는 (x, y)와 (x, y+1) 사이에 벽이 있는 것이다
 */
class Solution(val rowSize : Byte, val colSize : Byte, val k : Short, grid : Array<ByteArray>, informationOfWalls : ArrayList<ByteArray>) {
    companion object {
        const val ZERO : Byte = 0
        const val ONE : Byte = 1
        const val TWO : Byte = 2
        const val THREE : Byte = 3
        const val FOUR : Byte = 4
        const val FIVE : Byte = 5
    }

    private val cells = Array(rowSize.toInt()) { Array(colSize.toInt()) { Cell(0)} }    // 격자판, 온도와 벽의 정보를 가진다.
    private val heaters = ArrayList<Heater>()       // 온풍기의 위치
    private val locationToInvestigate = ArrayList<Location>()   // 온도를 조사해야하는 위치

    init {
        // 온풍기의 위치와 조사해야할 위치 정보를 읽어서 변수(heaters, locationOfInvestigate)에 저장한다.
        for(row in 0 until rowSize) {
            for(col in 0 until colSize) {
                when(grid[row][col]) {
                    ZERO -> {}
                    ONE -> {
                        heaters.add(Heater(Location(row, col), Direction.RIGHT))
                    }
                    TWO -> {
                        heaters.add(Heater(Location(row, col), Direction.LEFT))
                    }
                    THREE -> {
                        heaters.add(Heater(Location(row, col), Direction.UP))
                    }
                    FOUR -> {
                        heaters.add(Heater(Location(row, col), Direction.DOWN))
                    }
                    FIVE -> {
                        locationToInvestigate.add(Location(row, col))
                    }
                    else -> throw IllegalArgumentException("grid 에는 0~5까지 정보 만 들어 갈 수 있습니다.")
                }
            }
        } /* end of for */

        // 변수(cells)에 벽의 정보를 기록한다.
        for(array in informationOfWalls) {
            /*
                다음 W개의 줄에는 벽의 정보가 주어지며, 이 정보는 세 정수 x, y, t로 이루어져 있다.
                t가 0인 경우 (x, y)와 (x-1, y) 사이에 벽이 있는 것이고, 1인 경우에는 (x, y)와 (x, y+1) 사이에 벽이 있는 것이다
             */
            val x = array[0] - 1    // 1를 빼는 이유는 배열의 인덱스가 0에서 출발하기 때문입니다.
            val y = array[1] - 1    // 위와 동일
            val t = array[2]

            if(t == ZERO) {
                var cell = cells[x][y]
                cell.wall.buildAWallOnTheTop()

                val t = x - 1
                cell = cells[t][y]
                cell.wall.buildAWallOnTheBottom()
            } else if(t == ONE) {
                var cell = cells[x][y]
                cell.wall.buildAWallOnTheRight()

                val t = y + 1
                cell = cells[x][t]
                cell.wall.buildAWallOnTheLeft()
            } /* end of for */
        }
    } /* end of init */

    /**
     * @return
     *  조사해야하는 지역의 온도가 모두 K 도 이상이면 참을 반환한다.
     */
    private fun temperatureIsAboveKDegrees() : Boolean {
        for(location in locationToInvestigate) {
            val temperature = cells[location.row.toInt()][location.col.toInt()].temperature
            if(temperature < k) return false
        }
        return true
    }

    /**
     * @return
     *  현제 위치가 격자판 안에 있으면 참을 반환한다.
     */
    private fun Location.isValid() : Boolean = (row in 0 until rowSize) && (col in 0 until colSize)

    /**
     * @return
     *  바람이 분 경우, 다음 바람들을 반환합니다. 벽 정보에 따라서 다음 바람이 다음 위치로 올 수도 있고 그렇지 않을 수도 있다.
     *
     *  하나의 바람이 불면, 세 개의 바람이 생겨 난다고 문제에서 이야기 하고 있다.
     *  바람의 방향(상하좌우) 에 따라서 바람은 방향을 조절하여 세개의 바람이 생겨난다.
     *  벽이 있는 경우, 막혀 있기 때문에 세 개의 바람 보다는 적은 개수의 바람이 생겨난다.
     */
    private fun Wind.blow() : HashSet<Wind> {
        if(temperature <= 1) return HashSet<Wind>()     // 현제의 바람의 온도가 1도 이하인 경우, 다음 바람은 불지 않는다.

        val nextWinds = HashSet<Wind>()         // 현제 바람이 불어서 발생하는 바람들을 저장하는 변수
        val t = (temperature - 1).toShort()     // 다음 바람의 온도를 결정한다.
        var cell : Cell

        when(direction) {
            // 바람이 윗쪽으로 부는 경우
            Direction.UP -> {
                val westLocation = location.west()
                if(westLocation.isValid()) {
                    cell = cells[westLocation.row.toInt()][westLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheTop() && cell.wall.isThereNoWallOnTheRight()) {
                        val northWestLocation = location.northWest()
                        if(northWestLocation.isValid()) nextWinds.add(Wind(northWestLocation, t, direction))
                    }
                }
                cell = cells[location.row.toInt()][location.col.toInt()]
                if(cell.wall.isThereNoWallOnTheTop()) {
                    val northLocation = location.north()
                    if(northLocation.isValid()) nextWinds.add(Wind(northLocation, t, direction))
                }

                val eastLocation = location.east()
                if(eastLocation.isValid()) {
                    cell = cells[eastLocation.row.toInt()][eastLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheTop() && cell.wall.isThereNoWallOnTheLeft()) {
                        val northEastLocation = location.northEast()
                        if(northEastLocation.isValid()) nextWinds.add(Wind(northEastLocation, t, direction))
                    }
                }
            }
            // 바람이 아랫쪽으로 부는 경우
            Direction.DOWN -> {
                val westLocation = location.west()
                if(westLocation.isValid()) {
                    cell = cells[westLocation.row.toInt()][westLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheBottom() && cell.wall.isThereNoWallOnTheRight()) {
                        val southWestLocation = location.southWest()
                        if(southWestLocation.isValid()) nextWinds.add(Wind(southWestLocation, t, direction))
                    }
                }

                cell = cells[location.row.toInt()][location.col.toInt()]
                if(cell.wall.isThereNoWallOnTheBottom()) {
                    val southLocation = location.south()
                    if(southLocation.isValid()) nextWinds.add(Wind(southLocation, t, direction))
                }

                val eastLocation = location.east()
                if(eastLocation.isValid()) {
                    cell = cells[eastLocation.row.toInt()][eastLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheBottom() && cell.wall.isThereNoWallOnTheLeft()) {
                        val southEastLocation = location.southEast()
                        if(southEastLocation.isValid()) nextWinds.add(Wind(southEastLocation, t, direction))
                    }
                }
            }
            // 바람이 오른쪽으로 부는 경우
            Direction.RIGHT -> {
                val northLocation = location.north()
                if(northLocation.isValid()) {
                    cell = cells[northLocation.row.toInt()][northLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheBottom() && cell.wall.isThereNoWallOnTheRight()) {
                        val northEastLocation = location.northEast()
                        if(northEastLocation.isValid()) nextWinds.add(Wind(northEastLocation, t, direction))
                    }
                }

                cell = cells[location.row.toInt()][location.col.toInt()]
                if(cell.wall.isThereNoWallOnTheRight()) {
                    val eastLocation = location.east()
                    if(eastLocation.isValid()) nextWinds.add(Wind(eastLocation, t, direction))
                }

                val southLocation = location.south()
                if(southLocation.isValid()) {
                    cell = cells[southLocation.row.toInt()][southLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheTop() && cell.wall.isThereNoWallOnTheRight()) {
                        val southEastLocation = location.southEast()
                        if(southEastLocation.isValid()) nextWinds.add(Wind(southEastLocation, t, direction))
                    }
                }
            }
            // 바람이 왼쪽으로 부는 경우
            Direction.LEFT -> {
                val northLocation = location.north()
                if(northLocation.isValid()) {
                    cell = cells[northLocation.row.toInt()][northLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheBottom() && cell.wall.isThereNoWallOnTheLeft()) {
                        val northWestLocation = location.northWest()
                        if(northWestLocation.isValid()) nextWinds.add(Wind(northWestLocation, t, direction))
                    }
                }

                cell = cells[location.row.toInt()][location.col.toInt()]
                if(cell.wall.isThereNoWallOnTheLeft()) {
                    val westLocation = location.west()
                    if(westLocation.isValid()) nextWinds.add(Wind(westLocation, t, direction))
                }

                val southLocation = location.south()
                if(southLocation.isValid()) {
                    cell = cells[southLocation.row.toInt()][southLocation.col.toInt()]
                    if (cell.wall.isThereNoWallOnTheTop() && cell.wall.isThereNoWallOnTheLeft()) {
                        val southWestLocation = location.southWest()
                        if(southWestLocation.isValid()) nextWinds.add(Wind(southWestLocation, t, direction))
                    }
                }
            }
        }

        return nextWinds
    }

    /**
     * 바람은 각 줄 마다 온도가 다르다.
     * 처음 생겨나는 바람은 5도 그리고 다음 줄로 갈 수록 1도씩 줄고, 온도가 1도 이면 더 이상 바람은 생겨나지 않는다.
     *
     * Set<Wind> 도는 같은 온도의 바람을 의미한다.
     *
     * @return
     *  한 줄의 바람이 불었을 때, 다음 바람들을 반환한다.
     */
    private fun Set<Wind>.blow() : HashSet<Wind> {
        val answerSet = HashSet<Wind>()

        for(wind in this) {
            answerSet.addAll(wind.blow())       // Set 을 사용하기 때문에 동일한 바람은 하나만 저장된다.
        }
        return answerSet
    }

    /**
     * 온풍기가 송풍을 해, 비림이 도착하고 곳의 온도를 상승시킨다.
     */
    private fun Heater.blow() {
        val answerSet = HashSet<Wind>()
        var firstWind : Wind

        when(directionOfWind) {
            Direction.RIGHT -> {
                val locationOfFirstWind = location.east()
                if(!locationOfFirstWind.isValid()) return
                firstWind = Wind(locationOfFirstWind, 5, Direction.RIGHT)
            }
            Direction.LEFT -> {
                val locationOfFirstWind = location.west()
                if(!locationOfFirstWind.isValid()) return
                firstWind = Wind(locationOfFirstWind, 5, Direction.LEFT)
            }
            Direction.DOWN -> {
                val locationOfFirstWind = location.south()
                if(!locationOfFirstWind.isValid()) return
                firstWind = Wind(locationOfFirstWind, 5, Direction.DOWN)
            }
            Direction.UP -> {
                val locationOfFirstWind = location.north()
                if(!locationOfFirstWind.isValid()) return
                firstWind = Wind(locationOfFirstWind, 5, Direction.UP)
            }
        }

        var currentWinds = HashSet<Wind>()
        currentWinds.add(firstWind)

        for(i in 0 until 5) {
            answerSet.addAll(currentWinds)
            val set = currentWinds.blow()
            if(set.isEmpty()) break
            currentWinds = set
        }

        for(wind in answerSet) {
            var t = cells[wind.location.row.toInt()][wind.location.col.toInt()].temperature
            t = (t + wind.temperature).toShort()
            cells[wind.location.row.toInt()][wind.location.col.toInt()].temperature = t
        }
    }

    /**
     * 온도를 조절한다.
     *
     * 예를 들어 배열 3x3 을 보면
     *      A00 A01 A02
     *      A10 A11 A12
     *      A20 A21 A22  <- 배열의 인덱스를 표현 했음
     * 계산은 (A01, A11), (A10, A11), (A11, A12), (A11, A21) 쌍의 차를 계산하는데, 한번도 계산한 적이 없는 쌍만 다시 계산한다.
     * 한번도 계산한 적이 없는 쌍을 찾는 방법은 Set에 모던 계산해야 하는 쌍을 저장해 두고,
     * Set에 쌍이 있으면 계산하고, Set에서 그 쌍을 지운다.
     * Set에 쌍이 없으면 계산하지 않는다.
     * 그리고 한 칸 씩 이동하면서 계산하여, 온도의 증가값과 감소값 만을 저장한다.
     */
    private fun controlTheTemperature() {
        // 온도 차이로 감소 또는 증가해야 하는 값을 저장하는 변수, 증가면 양수, 감소면 음수로 정의 한다.
        val diff = Array(rowSize.toInt()) { ShortArray(colSize.toInt()) { 0 } }
        /*
         배열에서 계산을 해야 하는 쌍을 저장합니다.
         계산을 진행하는 중 이 곳에 있는 쌍 만 계산을 한다.
         계산 후에는 이 곳에서 방금 계산한 쌍을 지워서 다시 계산되지 않도록 한다.
         Pair 에 저장된 순서가 중요하다. 두개의 Location 의 순서가 뒤바뀌면 함수가 진행되는 도중에 찾을 수 없기 때문이다.
         */
        val pairs = HashSet<Pair<Location, Location>>()
        // Wall 이 없음을 판단하는데 사용한다.
        val isNoWalls = arrayOf(Wall::isThereNoWallOnTheLeft, Wall::isThereNoWallOnTheRight, Wall::isThereNoWallOnTheTop, Wall::isThereNoWallOnTheBottom)

        for(row in 0 until rowSize) {
            for(col in 0 until (colSize - 1)) {
                // 왼쪽에서 오른쪽으로 가는 순서로 Pair.first, Pair.second 가 정의 되어 있다는 것에 대해 주의 해야 한다.
                // 반대 순서로 했을 경우, set.contains 에서 찾지 못하게 된다.
                pairs.add(Pair(Location(row, col), Location(row, col + 1)))
            }
        }
        for(col in 0 until colSize) {
            for(row in 0 until (rowSize - 1)) {
                // 위에서 아래로 가는 순서로 Pair.first, Pair.second가 정의 되어 있다는 것에 대해 주의 해야 한다.
                pairs.add(Pair(Location(row, col), Location(row + 1, col)))
            }
        }

        /**
         * diff 변수에 온도가 올라가는 값(양수), 온도가 내려가는 값(음수)를 저장한다.
         * diff 값이 모두 모이면, 온도 조절을 할 때, 이 값을 cells 의 temperature 에 모두 더해 준다.
         * diff 값이 양수이면 온도가 증가하고 음수이면 온도가 감사한다.
         *
         * @param
         *  centerLocation      : 중심 위치
         *  location            : 중심 위치에 인접한 위치들(상하좌우)
         *  ordering            : Pair(centerLocation, location) 로 검색하는 경우 true
         *                      : Pair(location, centerLocation) 로 검색하는 경우 false
         *  function            : 벽이 없는지는 확인하는 함수
         */
        fun calculate(centerLocation : Location, location : Location, ordering : Boolean, function : KFunction1<Wall, Boolean>) {
            val cell1 = cells[centerLocation.row.toInt()][centerLocation.col.toInt()]
            val pair : Pair<Location, Location>

            if(ordering) pair = Pair(centerLocation, location) else pair = Pair(location, centerLocation)

            if(location.isValid() && function.invoke(cell1.wall) && pairs.contains(pair)) {
                val cell2 = cells[location.row.toInt()][location.col.toInt()]
                val t1 = cell1.temperature
                val t2 = cell2.temperature
                val d = abs(t1 - t2) / 4

                if(t1 > t2) {
                    diff[centerLocation.row.toInt()][centerLocation.col.toInt()] = (diff[centerLocation.row.toInt()][centerLocation.col.toInt()] - d).toShort()
                    diff[location.row.toInt()][location.col.toInt()] = (diff[location.row.toInt()][location.col.toInt()] + d).toShort()
                } else if(t1 < t2) {
                    diff[centerLocation.row.toInt()][centerLocation.col.toInt()] = (diff[centerLocation.row.toInt()][centerLocation.col.toInt()] + d).toShort()
                    diff[location.row.toInt()][location.col.toInt()] = (diff[location.row.toInt()][location.col.toInt()] - d).toShort()
                }
                pairs.remove(pair)
            }
        } /* end of calculate */

        fun calculate(row : Int, col : Int) {
            val centerLocation = Location(row, col)
            calculate(centerLocation, centerLocation.west(), false, isNoWalls[0])
            calculate(centerLocation, centerLocation.east(), true, isNoWalls[1])
            calculate(centerLocation, centerLocation.north(), false, isNoWalls[2])
            calculate(centerLocation, centerLocation.south(), true, isNoWalls[3])
        }

        for(row in 0 until rowSize) {
            for(col in 0 until colSize) {
                calculate(row, col)
            }
        }

        // 변화되는 온도를 반영한다.
        for(row in 0 until rowSize) {
            for(col in 0 until colSize) {
                cells[row][col].temperature = (cells[row][col].temperature + diff[row][col]).toShort()
                if(cells[row][col].temperature < 0) cells[row][col].temperature = 0
            }
        }
    }

    /**
     * 가장 바깥쪽 라인의 온도를 0 이상이면 1도 낮춘다.
     */
    private fun lowerByOneDegree() {
        fun a() {
            val row = 0
            for(col in 1 until colSize) {
                val cell = cells[row][col]
                if(cell.temperature > 0) --cell.temperature
            }
        }

        fun b() {
            val col = colSize - 1
            for(row in 1 until rowSize) {
                val cell = cells[row][col]
                if(cell.temperature > 0) --cell.temperature
            }
        }

        fun c() {
            val row = rowSize - 1
            for(col in colSize - 2 downTo  0) {
                val cell = cells[row][col]
                if(cell.temperature > 0) --cell.temperature
            }
        }

        fun d() {
            val col = 0
            for(row in rowSize - 2 downTo 0) {
                val cell = cells[row][col]
                if(cell.temperature > 0) --cell.temperature
            }
        }
        a()
        b()
        c()
        d()
    }

    /**
     * 모의 시험을 해서 문제의 정답인 초콜릿을 먹은 개수를 반환한다.
     *
     * @return
     *  문제의 정답을 반환한다.
     */
    fun simulate() : Int {
        var chocolate = 0       // 초콜릿의 개수

        while(true) {
            for(heater in heaters) heater.blow()        // 온풍기에서 바람이 나와서 온도를 올린다.

            controlTheTemperature()                     // 온도 조절을 한다.

            lowerByOneDegree()                          // 격자의 가장 바깥 쪽 라인의 온도가 1도 이상 크면 1도 줄인다.

            chocolate++                                 // 초콜렛을 하나 먹는다

            if(chocolate > 100) return 101              // 초콜렛을 백개 이상 먹었으면 무조건 101개를 반환한다.

            if(temperatureIsAboveKDegrees()) return chocolate       // 조자해야할 위치의 온도가 모두 K도 이상이면 먹은 초콜렛 개수를 반환한다.
        }
    }
}   /* end of Solution */

fun main() {
    /*
        r : 격자판의 행의 크기
        c : 격자판의 열의 크기
        k : 조사해야하는 칸의 온도가 k도 이상이면
     */
    val (r, c, k) = readln().split(Regex("\\s+")).map { it.toShort() }

    /*
        격자판 위의 정보가 제공된다.

        0: 빈 칸
        1: 방향이 오른쪽인 온풍기가 있음
        2: 방향이 왼쪽인 온풍기가 있음
        3: 방향이 위인 온풍기가 있음
        4: 방향이 아래인 온풍기가 있음
        5: 온도를 조사해야 하는 칸
     */
    val grid = Array(r.toInt()) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }

    // 벽의 개수
    val w = readln().toShort()

    /*
        벽의 정보가 제공된다.

        다음 W개의 줄에는 벽의 정보가 주어지며, 이 정보는 세 정수 x, y, t로 이루어져 있다.
        t가 0인 경우 (x, y)와 (x-1, y) 사이에 벽이 있는 것이고, 1인 경우에는 (x, y)와 (x, y+1) 사이에 벽이 있는 것이다
     */
    val informationOfWalls = ArrayList<ByteArray>()
    for(i in 0 until w) {
        val array = readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
        informationOfWalls.add(array)
    }

    println("${Solution(r.toByte(), c.toByte(), k, grid, informationOfWalls).simulate()}")
}
