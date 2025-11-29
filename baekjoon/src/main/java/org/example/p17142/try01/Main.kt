package org.example.p17142.try01

import java.util.Objects

const val EMPTY : Byte = 0      // 비어있는 공간
const val WALL : Byte = 1       // 벽
const val VIRUS : Byte = 2      // 비활성 바이러스

/**
 * 연구소 내의 위치로 나타낼 때 사용하는 클래스
 *
 * @param
 *  row : 열의 위치
 *  col : 행의 위치
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Byte) : this(row.toByte(), col)

    /**
     * @return
     * 현제 위치의 왼쪽 위치를 반환한다.
     */
    fun left() : Location = Location(row, col - 1)

    /**
     * @return
     *  현제 위치의 오른족 위치를 반환한다.
     */
    fun right() : Location = Location(row, col + 1)

    /**
     * @return
     *  현제 위치의 위쪽 위치를 반환한다.
     */
    fun up() : Location = Location(row - 1, col)

    /**
     * @return
     *  현제 위치의 아래쪽 위치를 반환한다.
     */
    fun down() : Location = Location(row + 1, col)

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

    override fun toString(): String {
        return "Location(row=$row, col=$col)"
    }
}


/**
 * 문제를 해결하는 클래스
 *
 * @param
 *  size                : 연구소(researchInstitute)의 크기
 *  count               : 놓을 수 있는 바이러스의 개수
 *  researchInstitute   : 연구소의 정보
 */
class Solution(val size : Byte, val count : Byte, val researchInstitute : Array<ByteArray>) {
    private val locationsOfTheVirus : Array<Location>  // 연구소 내의 바이러스의 위치들
    private val numberOfEmptyLocations : Int    // 연구소 내에 빈 공간의 개수

    init {
        val set = HashSet<Location>()
        var cnt = 0
        for(row in 0 until size) {
            for(col in 0 until size) {
                if(researchInstitute[row][col] == VIRUS) set.add(Location(row, col))
                if(researchInstitute[row][col] == EMPTY) cnt++
            }
        }
        locationsOfTheVirus = set.toTypedArray()
        numberOfEmptyLocations = cnt
    }

    private fun Location.isValid() : Boolean = (row in 0 until size) && (col in 0 until size)

    /**
     * bfs(너비 우선 탐색)에서 사용되는 클래스, 탐색하는 과정에서 현제의 상태를 저장한다.
     *
     * @param
     *  currentLocation : 현제의 위치
     *  arrivalTime     : 도착한 시간
     *
     */
    class Piece(val currentLocation : Location, val arrivalTime : Int)

    /**
     * 너비 우선 탐색 알고리즘 입니다.
     *
     * @param
     *  set : 활성 바이러스의 위치들
     *
     * @return
     *  연구소를 바이러스로 모두 체우는데 걸리는 최소 시간을 반환한다. 연구소 모두를 바이러스로 체울 수 없을 때 -1를 반환한다.
     */
    private fun bfs(set : Set<Location>) : Int {
        val visited = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }      // 방문했으면 참
        val inQueue = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }      // 큐에 담겨 있으면 참

        val queue = ArrayDeque<Piece>()
        for(location in set) {
            queue.addLast(Piece(location, 0))
            inQueue[location.row.toInt()][location.col.toInt()] = true
        }

        var cnt = 0       // 빈 공간이 활성 바이러스로 변한 수

        while(queue.isNotEmpty()) {
            val piece = queue.removeFirst()
            val currLocation = piece.currentLocation

            visited[currLocation.row.toInt()][currLocation.col.toInt()] = true
            inQueue[currLocation.row.toInt()][currLocation.col.toInt()] = false

            if(researchInstitute[currLocation.row.toInt()][currLocation.col.toInt()] == EMPTY) cnt++

            if(cnt == numberOfEmptyLocations) return piece.arrivalTime

            val nextLocations = arrayOf(currLocation.left(), currLocation.right(), currLocation.up(), currLocation.down())

            for(nextLocation in nextLocations) {
                if(nextLocation.isValid()) {
                    if(!visited[nextLocation.row.toInt()][nextLocation.col.toInt()] &&
                        !inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()] &&
                        researchInstitute[nextLocation.row.toInt()][nextLocation.col.toInt()] != WALL) {
                        queue.addLast(Piece(nextLocation, piece.arrivalTime + 1))
                        inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()] = true
                    }
                }
            }
        }
        return -1
    }

    /**
     * 시뮬레이션 한다.
     *
     * @return
     *  문제의 정답인 최소시간을 반환한다.
     */
    fun simulate() : Int {
        var result = -1
        val set = HashSet<Location>()       // 선택된 위치들
        val lastDepth = count.toInt()       // dfs(깊이 우선 탐색)의 마지막 깊이

        /**
         * dfs(깊이 우선 탐색) 알고리즘
         * 지도에서 주어지는 바이러스를 놓을 수 있는 위치들 중 임의의 n개(문제에서 주어진다.)를 선택하여,
         * Set에 넣는다.
         * Set을 bfs메서드로 보내어 실제로 몇 초 만에 바이러스가 모두 확산하는지 확인하고,
         * 그 정도를 토대로 최소 시간을 구한다.
         */
        fun dfs(startIndex : Int, depth : Int) {
            if(depth == lastDepth) {
                val t = bfs(set)
                if(t == -1) return
                else {
                    if (result == -1) result = t
                    else {
                        if (t < result) result = t
                    }
                }
            } else {
                val lastIndex = locationsOfTheVirus.lastIndex - lastDepth + 1 + depth
                for(i in startIndex..lastIndex) {
                    val location = locationsOfTheVirus[i]
                    set.add(location)
                    dfs(i + 1, depth + 1)
                    set.remove(location)
                }
            }
        }

        dfs(0, 0)

        return result
    }
}

fun main() {
    val (n, m) = readln().split(Regex("\\s+")).map { it.toByte() }      // n : 연구소의 크기, m : 놓을 수 있는 바이러스의 개수
    val researchInstitute = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    } // 연구소의 정보

    println(Solution(n, m, researchInstitute).simulate())
}
