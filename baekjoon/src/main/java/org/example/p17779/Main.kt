package org.example.p17779

import java.util.Arrays
import kotlin.reflect.KFunction2

/**
 * 문제 출처 : https://www.acmicpc.net/problem/17779
 *
 * @author
 *  조홍제
 */
const val numberOfConstituencies = 5        // 선거구의 개수

/**
 * 재현시에서 구역의 위치를 나타내는 클래스입니다.
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    fun left() : Location = Location(row, col - 1)
    fun right() : Location = Location(row, col + 1)
    fun up() : Location = Location(row - 1, col)
    fun down() : Location = Location(row + 1, col)
}

/**
 * 문제를 해결하는 클래스
 *
 * @param
 *  size : 재현시의 크기
 *  population : 구역의 인구수
 */
class Solution(val size : Byte, val population : Array<ByteArray>) {
    private val UNKNOWN : Byte = 6
    private val ZONE5 : Byte = 5
    /*
    구역 정보를 저장하는 변수, 처음에는 모두 6번 구역으로 구분한다.
    6번 구역은 미지의 구역이다.(내가 정했음)
    경계선을 그리고, bfs 알고리즘으로 각각의 1~5 구역으로 나눈다.
    나중에 이 정보를 이용하여 선거구의 인구수의 최대값과 최소값을 구하고 그 차를 구한다.
     */
    private val districts = Array(size.toInt()) { ByteArray(size.toInt()) { UNKNOWN } }

    private fun Location.isValid() : Boolean = row in 0 until size && col in 0 until size

    /**
     * districts 변수를 UNKNOWN 으로 초기화 한다.
     */
    private fun initDistricts() {
        for(arr in districts) {
            Arrays.fill(arr, UNKNOWN)
        }
    }

    /**
     * districts 배열에 선거구의 경계를 그린다. 선거구의 경계는 5번 구역이라 5를 넣는다.
     *
     * 1번 경계선: (x, y), (x+1, y-1), ..., (x+d1, y-d1)
     * 2번 경계선: (x, y), (x+1, y+1), ..., (x+d2, y+d2)
     * 3번 경계선: (x+d1, y-d1), (x+d1+1, y-d1+1), ... (x+d1+d2, y-d1+d2)
     * 4번 경계선: (x+d2, y+d2), (x+d2+1, y+d2-1), ..., (x+d2+d1, y+d2-d1)
     */
    private fun drawBoundaries(x : Int, y : Int, d1 : Int, d2 : Int) {
        val r = x - 1   // 배열의 처음 인덱스가 0이기 때문에 1를 빼준다
        val c = y - 1
        var row : Int
        var col : Int

        //1번 경계선
        for(distance in 0..d1) {
            row = r + distance
            col = c - distance
            districts[row][col] = 5
        }

        //2번 경계선
        for(distance in 0..d2) {
            row = r + distance
            col = c + distance
            districts[row][col] = 5
        }

        //3번 곙계선
        var tx = r + d1
        var ty = c - d1
        for(distance in 0..d2) {
            row = tx + distance
            col = ty + distance
            districts[row][col] = 5
        }

        //4번 곙계선
        tx = r + d2
        ty = c + d2
        for(distance in 0..d1) {
            row = tx + distance
            col = ty - distance
            districts[row][col] = 5
        }
    }

    /**
     * districts 변수에 구역이 몇번 선거구인지 마킹을 한다.
     * 1~4번 선거구는 bfs(너비 우선 탐색)알고리즘으로 찾고 5번 선거구는 나머지가 된다.
     *
     * 1번 선거구: 1 ≤ r < x+d1, 1 ≤ c ≤ y, 1번 경계선의 왼쪽 위
     * 2번 선거구: 1 ≤ r ≤ x+d2, y < c ≤ N, 2번 경계선의 오른쪽 위
     * 3번 선거구: x+d1 ≤ r ≤ N, 1 ≤ c < y-d1+d2, 3번 경계선의 왼쪽 아래
     * 4번 선거구: x+d2 < r ≤ N, y-d1+d2 ≤ c ≤ N, 4번 경계선의 오른쪽 아래
     */
    private fun markTheZoneNumber(x : Int, y : Int, d1 : Int, d2 : Int) {
        /**
         * 1번 선거구
         */
        fun checkFirst(r : Int, c : Int) : Boolean {
            return r in 1 until (x + d1) && c in 1..y
        }

        /**
         * 2번 선거구
         */
        fun checkSecond(r : Int, c : Int) : Boolean {
            return r in 1..(x + d2) && c in (y + 1)..size
        }

        /**
         * 3번 선거구
         */
        fun checkThird(r : Int, c : Int) : Boolean {
            return r in (x + d1)..size && c in 1..(y-d1 + d2)
        }

        /**
         * 4번 선거구
         */
        fun checkForth(r : Int, c : Int) : Boolean {
            return (x + d2) < r && r <= size && c in (y - d1 + d2)..size
        }

        /**
         * 각각의 구역을 선거구번호(zoneNumber : 1~4)로 마킹을 한다.
         * bfs(너비 우선 탐색) 알고리즘
         */
        fun bfs(startLocation : Location, zoneNumber : Byte, checker : KFunction2<Int, Int, Boolean>) {
            val visited = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }      // 방문했으면 true
            val inQueue = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }      // 큐 안에 있으면 true

            val queue = ArrayDeque<Location>()
            queue.addLast(startLocation)
            inQueue[startLocation.row.toInt()][startLocation.col.toInt()] = true

            while(queue.isNotEmpty()) {
                val currentLocation = queue.removeFirst()
                districts[currentLocation.row.toInt()][currentLocation.col.toInt()] = zoneNumber
                visited[currentLocation.row.toInt()][currentLocation.col.toInt()] = true
                inQueue[currentLocation.row.toInt()][currentLocation.col.toInt()] = false

                val nextLocations = arrayOf(currentLocation.left(), currentLocation.right(), currentLocation.up(), currentLocation.down())

                for(nextLocation in nextLocations) {
                    if(nextLocation.isValid()) {
                        if(checker.invoke(nextLocation.row + 1, nextLocation.col + 1)) {
                            if(!visited[nextLocation.row.toInt()][nextLocation.col.toInt()]
                                && !inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()]
                                && districts[nextLocation.row.toInt()][nextLocation.col.toInt()] != ZONE5) {
                                queue.addLast(nextLocation)
                                inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()] = true
                            }
                        }
                    }
                }
            }
        }

        val checkers = arrayOf(::checkFirst, ::checkSecond, ::checkThird, ::checkForth)
        val startLocations = arrayOf(
            Location(0, 0),
            Location(0, size - 1),
            Location(size - 1, 0),
            Location(size - 1, size - 1)
        )

        for(i in 0..checkers.lastIndex) {
            bfs(startLocations[i],(i + 1).toByte(), checkers[i])
        }
    }

    /**
     * @param
     *  x, y : 경계의 기준점
     *  d1, d2 : 경계의 길이
     *
     * @return
     *  5개로 나누어진 경계에 포한된 인구수의 최대값과 최소값의 차를 반환합니다.
     */
    private fun difference(x : Int, y : Int, d1 : Int, d2 : Int) : Int {
        initDistricts()
        drawBoundaries(x, y, d1, d2)
        markTheZoneNumber(x, y, d1, d2)

        val total = IntArray(numberOfConstituencies) { 0 }

        for(row in 0 until size) {
            for(col in 0 until size) {
                var number = districts[row][col]
                if(number == UNKNOWN) --number      // UNKNOWN 구역은 5번 구역이다.
                total[number - 1] = total[number - 1] + population[row][col]
            }
        }

        val max = Arrays.stream(total).max()
        val min = Arrays.stream(total).min()

        return max.asInt - min.asInt
    }

    /**
     * 기준점 (x, y)와 경계의 길이 d1, d2를 정한다. (d1, d2 ≥ 1, 1 ≤ x < x+d1+d2 ≤ N, 1 ≤ y-d1 < y < y+d2 ≤ N)
     *
     * @return
     *  최대값과 최소값의 차의 최소값를 반환한다.(정답을 반환한다.)
     */
    fun simulate() : Int {
        var answer = Int.MAX_VALUE

        for(x in 1..size) {
            for(y in 1..size) {
                for(d2 in 1..(size - y)) {
                    for(d1 in 1.. (size - x - d2)) {
                        val t1 = x + d1 + d2
                        val t2 = y - d1
                        val t3 = y + d2

                        if(t1 in (x + 1)..size && 1 <= t2 && t3 <= size) {
                            val t = difference(x, y, d1, d2)
                            if(t < answer) answer = t
                        }
                    }
                }
            }
        }
        return answer
    }

}

fun main() {
    val n = readln().toByte()   // 재현시의 크기
    val population = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    } // 구역의 인구수

    println("${Solution(n, population).simulate()}")
}
