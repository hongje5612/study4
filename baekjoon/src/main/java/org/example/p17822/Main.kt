package org.example.p17822

/**
 * 출처 : https://www.acmicpc.net/problem/17822
 *
 * @author
 *  조홍제
 */

import java.util.Objects

/**
 * 원판을 어떡해 움직이는지를 나타내는 클래스
 *
 * @param
 *  x : x의 배수인 원판들
 *  d : 회전하는 방향, 0 이면 시계방향, 1 이면 반시계방향
 *  k : 움직이는 칸 수
 */
class Command(val x : Byte, val d : Byte, val k : Byte)

/**
 * 디스크 내의 숫자들의 위치를 표현하는 클래스
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
        return Objects.hash(row, col)
    }
}

/**
 * 문제를 해결하는 클래스
 *
 * 문제를 해결하는 방법은 명령에 따라 회전을 하고, bfs(너비 우선 탐색)로 인접한 칸들이 동일한지를 확인하고,
 * 동일하면 지우고, 그렇지 않으면 평균을 구하고 평균보다 크면 1을 줄이고, 평균보다 작으면 1을 증가시키는 과정을,
 * commands 의 마지막 명령까지 실행해는 것이다. 시뮬레이션을 하면된다.
 *
 * @param
 *  numberOfDisks   : 디스크의 개수
 *  numberOfNumbers : 디스크 한 장에 적혀 있는 숫자의 개수
 *  disks           : 디스크에 적혀 있는 숫자들의 정보
 *  commands        : 어떤 디스크를 몇 칸을 어떤 방향으로 회전하는지를 나타내는 변수
 */
class Solution(val numberOfDisks : Byte, val numberOfNumbers : Byte, val disks : Array<ShortArray>, val commands : Array<Command>) {
    companion object {
        val ZERO : Byte = 0
    }

    /**
     * 디스크를 시계방향으로 k 칸 회전합니다.
     *
     * @param
     *  theNumberOfDisk : 디스크의 번호
     *  k               : 회전하는 칸 수
     */
    private fun rotateClockwise(theNumberOfDisk : Byte, k : Byte) {
        val row = theNumberOfDisk - 1       // 배열의 인덱스가 0이기 때문에
        val queue = ArrayDeque<Short>()

        for(i in 0 until k) {
            queue.addLast(disks[row][numberOfNumbers - 1 - i])
        }

        var destination = numberOfNumbers - 1
        for(i in (destination - k) downTo 0) {
            disks[row][destination--] = disks[row][i]
        }

        destination = k - 1
        while(queue.isNotEmpty()) {
            disks[row][destination--] = queue.removeFirst()
        }
    }

    /**
     * 디스크를 반시계 방향으로 회전합니다.
     *
     * @param
     *  theNumberOfDisk : 디스크의 번호
     *  k               : 회전하는 칸 수
     */
    private fun rotateCounterclockwise(theNumberOfDisk : Byte, k : Byte) {
        val row = theNumberOfDisk - 1   // 배열의 인덱스가 0이기 때문에
        val queue = ArrayDeque<Short>()

        for(i in 0 until k) queue.addLast(disks[row][i])

        var destination = 0
        for(index in k..disks[row].lastIndex) disks[row][destination++] = disks[row][index]

        destination = disks[row].size - k
        while(queue.isNotEmpty()) {
            disks[row][destination++] = queue.removeFirst()
        }
    }

    /**
     * 현제 위치의 위쪽 위치를 반환한다.
     */
    private fun Location.up() : Location? = if(row == ZERO) null else Location(row - 1, col)

    /**
     * 현제 위치의 아래쪽 위치를 반환한다.
     */
    private fun Location.down() : Location? = if(row == (numberOfDisks - 1).toByte()) null else Location(row + 1, col)

    /**
     * 현제 위치의 왼쪽 위치를 반환한다.
     */
    private fun Location.left() : Location = if(col == ZERO) Location(row, numberOfNumbers - 1) else Location(row, col - 1)

    /**
     * 현제 위치의 오른쪽 위치를 반환한다.
     */
    private fun Location.right() : Location = if(col == (numberOfNumbers - 1).toByte()) Location(row, ZERO) else Location(row, col + 1)


    /**
     * 문제에서 제시하는 첫번째 동작
     *
     * 번호가 xi의 배수인 원판을 di방향으로 ki칸 회전시킨다. di가 0인 경우는 시계 방향, 1인 경우는 반시계 방향이다.
     */
    private fun firstMovement(command : Command) {
        val rotation = arrayOf(::rotateClockwise, ::rotateCounterclockwise)

        var multiple = 1            // 배수
        while(true) {
            val number = command.x * multiple
            if(number > numberOfDisks) break
            else rotation[command.d.toInt()].invoke(number.toByte(), command.k)
            ++multiple
        }
    }

    /**
     * 문제에서 제시하는 두번째 동작
     *
     * 원판에 수가 남아 있으면, 인접하면서 수가 같은 것을 모두 찾는다.
     * 그러한 수가 있는 경우에는 원판에서 인접하면서 같은 수를 모두 지운다.
     * 없는 경우에는 원판에 적힌 수의 평균을 구하고, 평균보다 큰 수에서 1을 빼고, 작은 수에는 1을 더한다.
     *
     * 원판에 적힌 수가 0 인 경우 지운 수라고 생각한다.(내가 정했음, 코딩하는 사람마다 다르게 생각할 수 있음)
     */
    private fun secondMovement() {
        /**
         * 너비 우선 탐색 알고리즘으로 (row, col)에서 시작해서 각각의 인접한 지점을 방문하면서
         * 동일한 숫자가 나타나면 Set에 담는다
         *
         * @return
         *  동일한 숫자가 나오는 위치들을 반환합니다.
         *
         * @param
         *  row : bfs를 시작하는 행의 위치
         *  col : bfs를 시작하는 열의 위치
         */
        fun bfs(row : Byte, col : Byte) : Set<Location> {
            val visited = Array(numberOfDisks.toInt()) { BooleanArray(numberOfNumbers.toInt()) { false } }
            val inQueue = Array(numberOfDisks.toInt()) { BooleanArray(numberOfNumbers.toInt()) { false } }

            val returnSet = HashSet<Location>()
            val queue = ArrayDeque<Location>()
            queue.addLast(Location(row, col))
            inQueue[row.toInt()][col.toInt()] = true
            val startNumber = disks[row.toInt()][col.toInt()]

            while(queue.isNotEmpty()) {
                val currentLocation = queue.removeFirst()
                returnSet.add(currentLocation)
                visited[currentLocation.row.toInt()][currentLocation.col.toInt()] = true
                inQueue[currentLocation.row.toInt()][currentLocation.col.toInt()] = false

                val nextLocations = arrayOf(
                    currentLocation.up(), currentLocation.down(),
                    currentLocation.left(), currentLocation.right()
                )

                for(nextLocation in nextLocations) {
                    if(nextLocation != null) {
                        if(!visited[nextLocation.row.toInt()][nextLocation.col.toInt()] &&
                            !inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()] &&
                            disks[nextLocation.row.toInt()][nextLocation.col.toInt()] == startNumber) {
                            queue.addLast(nextLocation)
                            inQueue[nextLocation.row.toInt()][nextLocation.col.toInt()] = true
                        }
                    }
                }
            }
            return returnSet
        }

        /**
         * 동일한 숫자를 지운다.
         */
        fun removeSameNumber(set : Set<Location>) {
            for(location in set) disks[location.row.toInt()][location.col.toInt()] = 0
        }

        /**
         * 원판에 적힌 숫자의 평균을 구한다.
         *
         * @return
         *  평균을 반환한다.
         */
        fun calculateMean() : Double {
            var cnt = 0     // 지워지지 않는 숫자의 개수
            var total = 0   // 점수들의 합

            for(row in 0 until numberOfDisks) {
                for(col in 0 until numberOfNumbers) {
                    val t = disks[row][col]
                    if(t != 0.toShort()) { // 지운 숫자가 아니면
                        cnt++
                        total += t
                    }
                }
            }
            return total.toDouble() / cnt.toDouble()
        }

        /**
         * 평균보다 큰 수에서 1을 빼고, 작은 수에는 1을 더한다.
         */
        fun increaseAndDecrease() {
            val mean = calculateMean()

            for(row in 0 until numberOfDisks) {
                for(col in 0 until numberOfNumbers) {
                    val t = disks[row][col]
                    if(t == 0.toShort()) continue   // 지운 숫자면 다음 숫자를 확인한다.

                    if(t > mean) disks[row][col]--
                    else if(t < mean) disks[row][col]++
                }
            }
        }

        var con = false     // 한번이라고 동일한 숫자가 나온 적이 있으면 true
        for(row in 0 until numberOfDisks) {
            for(col in 0 until numberOfNumbers) {
                // 지운 숫자가 아니면
                if(disks[row][col] != 0.toShort()) {
                    val set = bfs(row.toByte(), col.toByte())
                    if(set.size > 1) {
                        removeSameNumber(set)
                        con = true
                    }
                }
            }
        }

        if(!con) increaseAndDecrease()
    }

    /**
     * @return
     *  문제에서 주어지는 두가지 행동을 수행한 후
     *  원반에 적히 숫자들의 합을 반환한다.
     */
    fun simulate() : Int {
        // 문제에서 주어지는 두가지 행동을 수행한다.
        for(command in commands) {
            firstMovement(command)
            secondMovement()
        }

        return sum() // 원반에 적힌 숫자들의 합을 반환한다.
    }

    /**
     * @return
     *  원반에 적히 숫자를의 합을 반환한다.
     */
    private fun sum() : Int {
        var result = 0

        for(row in 0 until numberOfDisks) {
            for(col in 0 until numberOfNumbers) {
                val t = disks[row][col]
                if(t == 0.toShort()) continue
                result += t
            }
        }
        return result
    }
}

fun main() {
    val (n, m, t) = readln().split(Regex("\\s+")).map { it.toByte() }
    val disks = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toShort() }.toShortArray()
    }
    val commands = Array(t.toInt()) {
        val (x, d, k) = readln().split(Regex("\\s+")).map { it.toByte() }
        Command(x, d, k)
    }

    val s = Solution(n, m, disks, commands)

    println("${s.simulate()}")
}
