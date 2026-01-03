package org.example.p23291

import java.util.Objects
import kotlin.math.abs

/**
 * 문제 출처 : https://www.acmicpc.net/problem/23291
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

/**
 * 2차원 배열 위의 위치를 나타내는 클래스
 *
 * @property
 *  row : 행의 위치
 *  column : 열의 위치
 */
class Location(val row : Byte, val column : Byte) {
    constructor(row : Int, column : Int) : this(row.toByte(), column.toByte())
    constructor(row : Byte, column : Int) : this(row, column.toByte())
    constructor(row : Int, column : Byte) : this(row.toByte(), column)

    /**
     * @return
     *  현제 위치의 왼쪽 위치를 반환한다.
     */
    fun left() : Location = Location(row, column - 1)

    /**
     * @return
     *  현제 위치의 오른쪽 위치를 반환한다.
     */
    fun right() : Location = Location(row, column + 1)

    /**
     * @return
     *  현제 위치의 위쪽 위치를 반환한다.
     */
    fun top() : Location = Location(row - 1, column)

    /**
     * @return
     *  현제 위치의 아랫쪽 위치를 반환한다.
     */
    fun bottom() : Location = Location(row + 1, column)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (row != other.row) return false
        if (column != other.column) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(row, column)
    }
}

/**
 * 문제를 해결하는 클래스
 *
 * @property
 *  size : 어항의 개수
 *  k : 물고기가 가장 많이 들어있는 어항과 가장 적게 들어있는 어항의 물고기 수 차이가 K 이하가 되려면 어항을 몇 번 정리해야하는지 출력한다
 *
 * @param
 *  arr : 초기 어항 정보
 */
class Solution(val size : Byte, val k : Byte, arr : IntArray) {
    /**
     * 어항들
     * 모던 어항들은 이 2차원 배열 안에 놓여 지게 된다.
     *
     * 초기치를 Int.MIN_VALUE로 설정한 것은 어항이 아님을 표현하기 위해서다. 단지 공간만 있다는 것을 표현한 것 임
     */
    private val fishTanks = Array(size.toInt()) { IntArray(size.toInt()) { Int.MIN_VALUE } }

    init {
        fishTanks[fishTanks.lastIndex] = arr
    }

    /**
     * @return
     *  현제 위치가 올바른 위치이면 참을 반환합니다.
     */
    private fun Location.isValid() : Boolean = row in 0 until size && column in 0 until size

    /**
     * @return
     *  현제 위치가 항아리가 놓인 위치이면 참을 반환한다.
     */
    private fun Location.isFishTank() : Boolean = fishTanks[row.toInt()][column.toInt()] != Int.MIN_VALUE

    /**
     * 어항을 옮긴다.
     */
    private fun moveFishTank(startingPoint : Location, destinationPoint : Location) {
        require(startingPoint != destinationPoint) { "어항을 옮기려는 위치와 출발위치가 동일합니다." }
        require(fishTanks[destinationPoint.row.toInt()][destinationPoint.column.toInt()] == Int.MIN_VALUE) { "도착지가 비어 있는 상태가 아닙니다."}

        val t = fishTanks[startingPoint.row.toInt()][startingPoint.column.toInt()]
        fishTanks[startingPoint.row.toInt()][startingPoint.column.toInt()] = fishTanks[destinationPoint.row.toInt()][destinationPoint.column.toInt()]
        fishTanks[destinationPoint.row.toInt()][destinationPoint.column.toInt()] = t
    }

    /**
     * 물고기가 가장 작은 어항들에 물고기를 한 마리 추가하고, 첫 번째 어항을 두번째 어항 위에 올려 놓는다.
     */
    private fun addOneFishToTheSmallestTankAndPlaceItOnTop() {
        val min = fishTanks[fishTanks.lastIndex].min()
        fishTanks[fishTanks.lastIndex].forEachIndexed { index, i ->
            if(i == min) fishTanks[fishTanks.lastIndex][index] = min + 1
        }
        val starting = Location(fishTanks.lastIndex, 0)
        moveFishTank(starting, starting.right().top())
    }

    /**
     * @return
     *  어항이 쌓여 있는 높이는 반환합니다.
     *
     * @param
     *  col : 열의 번호
     */
    private fun height(col : Int) : Int {
        var answer = 0

        for(row in fishTanks.lastIndex downTo 0) {
            if(fishTanks[row][col] != Int.MIN_VALUE) answer++
            else break
        }
        return answer
    }

    /**
     * 문제에서 제시 하는 첫 번째 공중 부양을 한 후, 시꼐 방향으로 90도 회전 한 후에 어항 위에 올려 놓은 작업을 한다.
     *
     * @return
     *  공중 부양한 후, 시계 방향으로 90도 회전해서 어항들을 올려 놓을 수 있으면 올려 놓고, 참을 반환합니다.
     *  그렇지 않으면 거짓을 반환합니다.
     */
    private fun levitateAndThenPlaceItOnTop1() : Boolean {
        /*
         *         +-+-+
         *         |3|4|
         *         +-+-+-+-+-+-+-+-+
         *         |4|6|9|4|5|7|9|1|
         *         +-+-+-+-+-+-+-+-+
         *  열 번호  2 3 4 5 6 7 8 9
         *
         *  위의 그림은 어항이 쌓이 있는 모양을 예를 들어습니다.
         *  이 경우 startIndex = 2, 이고 endIndex = 3 입니다.
         *  height = 2 입니다. 2행 2열을 공중부양합니다.
         */
        var startIndex = Int.MIN_VALUE      // 높이가 2 이상인 어항들이 있는 시작 열 인덱스
        var endIndex = Int.MIN_VALUE        // 높이가 2 이상인 어항들이 있는 마지막 열 인덱스
        var height  = 1                     // 높이가 2 이상인 어항들, 공중 부양 해야 하는 어항들의 높이

        for(col in 0 until size) {
            val h = height(col)
            if(h > 1) {
                height = h
                if(startIndex == Int.MIN_VALUE) {
                    startIndex = col
                    endIndex = col
                } else {
                    endIndex = col
                }
            }
        }

        /*
            공중 부양한 어항들을 시계방향으로 90도 회전한 후의 폭이 내려 놓을 위치에 있는 어항들의 폭보다 크면,
            공중 부양할 수 없다는 시그널인 false를 반환한다.
         */
        if(fishTanks.lastIndex - endIndex < height) return false

        /*
            높이가 2 이상인 어항들을 90도 시계 방향으로 회전해서 내려 놓을 위치에 내려 놓는다.
         */
        var destRow = fishTanks.lastIndex - 1
        for(col in endIndex downTo startIndex) {
            var destCol = endIndex + 1
            for(row in fishTanks.lastIndex downTo fishTanks.lastIndex - height + 1) {
                moveFishTank(Location(row, col), Location(destRow, destCol))
                destCol++
            }
            destRow--
        }
        return true
    }

    /**
     * 물고기 수를 조절하다.
     *
     * 1. 현제 위치 Location(row, col) 와
     *    Location(row, col).left(), Location(row, col).right(), Location(row, col).top(), Location(row, col).bottom() 과 비교 연산을 한다.
     *    비교 연산을 한 쌍을 기억해 두고, 다음에 다시 계산하지 않는다.
     *
     * 2. 현제 위치를 Location(row, col).right() 로 이동한다. 이동할 수 없으면 다음 줄 처음으로 간다.
     *
     * 3. 모두 비교 연산을 마칠 때까지 1 번 으로 돌아가서 연산을 한다.
     */
    private fun controlTheNumberOfFish() {

        val memory = HashMap<Location, MutableSet<Location>>()        // 계산했는지를 기억한다.
        val difference = Array(size.toInt()) { IntArray(size.toInt()) { 0 } }   // 물고기의 변화량을 저장한다.
        val nextLocationFunctions = arrayOf(Location::left, Location::right, Location::top, Location::bottom)

        for(row in 0 until size) {
            for(col in 0 until size) {
                val currentLocation = Location(row, col)
                if(currentLocation.isFishTank()) {
                    for (nextLocationFunc in nextLocationFunctions) {
                        val nextLocation = nextLocationFunc.invoke(currentLocation)
                        if(nextLocation.isValid() && nextLocation.isFishTank()) {
                            val alreadyCompare1 = memory[currentLocation]?.contains(nextLocation) ?: false
                            val alreadyCompare2 = memory[nextLocation]?.contains(currentLocation) ?: false
                            if(!alreadyCompare1 && !alreadyCompare2) {
                                val diff = abs(fishTanks[currentLocation.row.toInt()][currentLocation.column.toInt()] - fishTanks[nextLocation.row.toInt()][nextLocation.column.toInt()])
                                val d = diff / 5
                                if(d > 0) {
                                    if(fishTanks[currentLocation.row.toInt()][currentLocation.column.toInt()] > fishTanks[nextLocation.row.toInt()][nextLocation.column.toInt()]) {
                                        difference[currentLocation.row.toInt()][currentLocation.column.toInt()] -= d
                                        difference[nextLocation.row.toInt()][nextLocation.column.toInt()] += d
                                    } else if(fishTanks[currentLocation.row.toInt()][currentLocation.column.toInt()] < fishTanks[nextLocation.row.toInt()][nextLocation.column.toInt()]) {
                                        difference[currentLocation.row.toInt()][currentLocation.column.toInt()] += d
                                        difference[nextLocation.row.toInt()][nextLocation.column.toInt()] -= d
                                    }
                                }
                                val set1 = memory[currentLocation]
                                if(set1 != null) set1.add(nextLocation)
                                else memory[currentLocation] = mutableSetOf(nextLocation)
                                val set2 = memory[nextLocation]
                                if(set2 != null) set2.add(currentLocation)
                                else memory[nextLocation] = mutableSetOf(currentLocation)
                            }
                        }
                    }
                }
            }
        }

        for(row in 0 until size) {
            for(col in 0 until size) {
                val currentLocation = Location(row, col)
                if(currentLocation.isFishTank()) { fishTanks[row][col] += difference[row][col] }
            }
        }
    }

    /**
     * 공중 부양 했던 어항들을 바닥에 내려 놓는다.
     */
    private fun layTheFishTanksInTheRowOnTheFloor() {
        var destination = Location(fishTanks.lastIndex, 0)

        for(col in 0 until size) {
            val h = height(col)
            if(h > 1) {
                for(row in fishTanks.lastIndex downTo fishTanks.lastIndex - h + 1) {
                    moveFishTank(Location(row, col), destination)
                    destination = destination.right()
                }
            }
        }
    }

    /**
     * 두번째 공중 부양이 일어난다.
     */
    private fun levitateAndThenPlaceItOnTop2() {
        // 첫 번째 동작
        // 가운데를 중심으로 왼쪽 N/2개를 공중 부양시켜 전체를 시계 방향으로 180도 회전 시킨 다음, 오른쪽 N/2개의 위에 놓아야 한다.
        val middleIndex = size / 2 - 1
        var destination = Location(fishTanks.lastIndex - 1, size - 1)
        val row = fishTanks.lastIndex
        for(col in 0..middleIndex) {
            moveFishTank(Location(row, col), destination)
            destination = destination.left()
        }

        // 두 번째 동작
        // 가운데를 중심으로 왼쪽 N/2개를 공중 부양시켜 전체를 시계 방향으로 180도 회전 시킨 다음, 오른쪽 N/2개의 위에 놓아야 한다.
        val startCol = middleIndex + 1
        val width = size / 4
        val endCol = startCol + width - 1
        for(row in fishTanks.lastIndex - 1 .. fishTanks.lastIndex) {
            val height = height(size - 1)
            var destination = Location(fishTanks.lastIndex - height, size - 1)
            for(col in startCol .. endCol) {
                moveFishTank(Location(row, col), destination)
                destination = destination.left()
            }
        }
    }

    /**
     * 어항 정리
     */
    private fun fishTankOrganization() {
        addOneFishToTheSmallestTankAndPlaceItOnTop()        // 가장 물고기가 작은 어항에 물고기 한 마리 추가 후, 첫번째 어항에 올려 놓기
        while(levitateAndThenPlaceItOnTop1()) { /* nop */ } // 첫번째 공중 부양
        controlTheNumberOfFish()                            // 물고기 수 조절
        layTheFishTanksInTheRowOnTheFloor()                 // 바닥에 내려 놓기
        levitateAndThenPlaceItOnTop2()                      // 두번째 공중 부양
        controlTheNumberOfFish()                            // 물고기 수 조절
        layTheFishTanksInTheRowOnTheFloor()                 // 바닥에 내려 놓기
    }

    /**
     * 물고기가 가장 많이 들어있는 어항과 가장 적게 들어있는 어항의 물고기 수 차이가 K 이하가 되려면
     */
    private fun meetTheConditions() : Boolean {
        val fishTanks = fishTanks[size - 1]
        val max = fishTanks.max()
        val min = fishTanks.min()

        return (max - min) <= k
    }

    /**
     * 어항 작업을 모의 테스한다.
     *
     * @return
     *  물고기가 가장 많이 들어있는 어항과 가장 적게 들어있는 어항의 물고기 수 차이가 K 이하가 되려면 어항을 몇 번 정리해야하는지 반환한다.
     */
    fun simulate() : Int {
        var round = 0

        while(true) {
            if(meetTheConditions()) return round
            fishTankOrganization()
            round++
        }
    }
}

fun main() {
    // n : 어항의 개수
    // k : 물고기가 가장 많이 들어있는 어항과 가장 적게 들어있는 어항의 물고기 수 차이가 K 이하가 되려면 어항을 몇 번 정리해야하는지 출력한다
    val (n, k) = readln().split(Regex("\\s+")).map { it.toByte() }
    val arr = readln().split(Regex("\\s+")).map { it.toInt() }.toIntArray()
    println("${Solution(n, k, arr).simulate()}")
}
