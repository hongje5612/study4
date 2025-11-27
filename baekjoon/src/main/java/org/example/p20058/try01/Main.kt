package org.example.p20058.try01

import kotlin.math.pow

/**
 * 출처 : https://www.acmicpc.net/problem/20058
 * 제목 : 마법사 상어와 파이어스톰
 */

/**
 * 격자판(배열) 에서 사용되는 위치 정보를 저장합니다.
 */
class Location(val row : Byte, val col : Byte)


/**
 * @param
 * n : 격자판의 크기를 나타내는 변수, 실제 크기는 2^n
 * q : 파이어스톰을 시전한 횟수
 */
class Grid(n : Byte, q : Short) {

    companion object {
        val DX = arrayOf(0, 0, -1, 1)   // 상하좌우
        val DY = arrayOf(-1, 1, 0, 0)   // 상하좌우
    }

    // 격자판의 크기
    val size = (2.toDouble().pow(n.toInt())).toInt()

    // 얼음의 양의 정보를 담고 있는 2차원 배열
    private val board : Array<ByteArray> = Array(size) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }

    // 마법사 상어가 시진한 파이어스톰의 단계 값
    private val ls : ByteArray = readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()

    /**
     * @param
     * row1, col1 : board 배열의 임의의 위치, 선택 영역의 좌 상단
     * row2, col2 : board 배열의 임의의 위치, 선택 영역의 우 하단
     *
     * row1, col1 과(와) row2 col2 가 선택하고 있는 사각형 영역의 가장 바깥쪽 라인의 얼음 값들을 시계방향으로 90도 회전합니다.
     * (row1, col1) 와(과) (row2, col2)가 만드는 사각형의 영역은 항상 정사각형이다.(문제에서 그렇게 주어진다.)
     */
    private fun rotateClockwise(row1 : Int, col1 : Int, row2 : Int, col2 : Int) {
        val r1 = row1
        val c1 = col1
        val r2 = row1
        val c2 = col2
        val r3 = row2
        val c3 = col2
        val r4 = row2
        val c4 = col1
        for(i in 0 until(row2 - row1)) {
            val t = board[r1][c1 + i]

            board[r1][c1 + i] = board[r4 - i][c4]
            board[r4 - i][c4] = board[r3][c3 - i]
            board[r3][c3 - i] = board[r2 + i][c2]
            board[r2 + i][c2] = t
        }
    }

    /**
     * @param
     * (row, col)은 사각형 영역의 좌 상단의 위치입니다.
     * widthOrHeight 은(는) 사각형 영역의 폭 또는 높이입니다. (폭 == 높이)
     *
     * 사각형 영역의 모던 얼음값을 시계 방향으로 90도 회전합니다.
     */
    private fun rotateTheWholeThingClockwise(row : Int, col : Int, widthOrHeight : Int) {
        var r1 = row
        var c1 = col
        var r2 = row + widthOrHeight - 1
        var c2 = col + widthOrHeight - 1

        while(r1 < r2) {
            rotateClockwise(r1, c1, r2, c2)
            r1++
            c1++
            r2--
            c2--
        }
    }

    /**
     * @param
     * l : 마법사 상어가 시전한 파이어스톰의 단계값
     *
     * 격자 전체를 단계값으로 구분한 다음, 한 부분씩 시계방향으로 90도 회전합니다.
     */
    private fun rotateTheWholeThingClockwise(l : Byte) {
        val widthOrHeight = 2.toDouble().pow(l.toInt()).toInt()

        for(r in 0 until size step widthOrHeight) {
            for(c in 0 until size step widthOrHeight) {
                rotateTheWholeThingClockwise(r, c, widthOrHeight)
            }
        }
    }

    /**
     * (row, col) 위치가 격자판 안에 있으면 참, 그렇지 않으면 거짓
     */
    private fun isValidLocation(row : Int, col : Int) : Boolean = (row in 0 until size) && (col in 0 until size)

    /**
     * (row, col) 위치에서 얼음이 있는 칸 3개 또는 그 이상과 인접해있지 않은 칸이면 참을 반환합니다.
     */
    private fun shouldTheIceMelt(row : Int, col : Int) : Boolean {
        var count = 0   // 얼음과 접촉한 면

        for(index in 0 until DX.size) {
            val r = row + DY[index]
            val c = col + DX[index]

            if(isValidLocation(r, c)) {
                if(board[r][c] > 0) count++
            }
        }

        return count < 3
    }

    /**
     * 얼음과 삼면 이상 접한 곳의 얼음을 1씩 줄입니다.
     */
    private fun reduceIce() {
        val whereTheIceShouldMelt = ArrayList<Location>()   // 얼음이 1씩 녹아야하는 위치

        for(row in 0 until size) {
            for(col in 0 until size) {
                if(shouldTheIceMelt(row, col)) whereTheIceShouldMelt.add(Location(row.toByte(), col.toByte()))
            }
        }

        for(location in whereTheIceShouldMelt) {
            if(board[location.row.toInt()][location.col.toInt()] > 0) board[location.row.toInt()][location.col.toInt()]--
        }
    }

    /**
     * @param
     * l : 파이어스톰의 단계값
     * 파이어스톰을 시전합니다.
     */
    private fun fireStorm(l : Byte) {
        rotateTheWholeThingClockwise(l)
        reduceIce()
    }

    /**
     * 문제에서 주어진 과정을 실행합니다.
     */
    private fun simulate() {
        for(l in ls) {
            fireStorm(l)
        }
    }

    init {
        simulate()
    }

    /**
     *@return
     * 모던 파이어스톰이 시전된 후 얼음의 양을 반환합니다.
     */
    fun sumOfIce() : Long {
        var result = 0L

        for(row in 0 until size) {
            for(col in 0 until size) {
                if(board[row][col] > 0) result += board[row][col]
            }
        }
        return result
    }

    fun show() {
        for(row in 0 until size) {
            for(col in 0 until size) {
                print("${board[row][col]} ")
            }
            println()
        }
    }

    /**
     * 가장 큰 얼음 덩어리를 찾는 해결책을 제공하기 위한 클래스 입니다.
     *
     * bfs(너비 우선 탐색) 알고리즘을 여러번 실행해서 가장 큰 얼음 덩어리를 찾습니다.
     */
    inner class Solution {
        /**
         * 현제 모던 격자의 각각의 위치에서 bfs를 해야 한다. 방문한 적이 없음을 나타냅니다.
         */
        private val visited = Array(size) { BooleanArray(size) { false } }

        /**
         * bfs 알고리즘이 진행되는 도중의 위치가 queue에 담겨 있지는, 그렇지 않은지를 나타내는 변수
         */
        private val inQueue = Array(size) { BooleanArray(size) { false } }

        /**
         * @param
         * (row, col) 현제의 위치
         *
         * @return
         * 현제 위치의 얼음 덩어리의 크기를 반환합니다.
         *
         * bfs(너비 우선 탐색) 알고리즘을 이용합니다.
         */
        private fun sizeOfTheIceBlock(row : Int, col : Int) : Int {
            val queue = ArrayDeque<Location>()
            var answer = 0

            queue.addLast(Location(row.toByte(), col.toByte()))
            inQueue[row][col] = true

            while(queue.isNotEmpty()) {
                val currentLocation = queue.removeFirst()

                answer++
                inQueue[currentLocation.row.toInt()][currentLocation.col.toInt()] = false     // 큐에 담겨 있지 않다.
                visited[currentLocation.row.toInt()][currentLocation.col.toInt()] = true      // 방문했음, 더 이상 bfs를 할 필요가 없다.

                for(index in 0 until DX.size) {
                    val r = currentLocation.row + DY[index]
                    val c = currentLocation.col + DX[index]

                    // 위치가 Grid 내에 있고, 얼음이 남아 있으고, 큐에 담겨져 있지 않고, 방문한 적이 없다면
                    if(isValidLocation(r, c) && board[r][c] > 0 && !inQueue[r][c] && !visited[r][c]) {
                        queue.addLast(Location(r.toByte(), c.toByte()))
                        inQueue[r][c] = true    // queue 에 담겼음
                    }
                }
            }
            return answer
        }

        /**
         * @return
         * 가장 큰 얼음 덩어리의 크기를 반환합니다.
         */
        fun sizeOfTheLargestIceChunk() : Int {
            var answer = 0

            for(row in 0 until size) {
                for(col in 0 until size) {
                    if(!visited[row][col] && board[row][col] > 0) {
                        val t = sizeOfTheIceBlock(row, col)
                        if(t > answer) answer = t
                    }
                }
            }

            return answer
        }
    } // of Solution

    fun sizeOfTheLargestIceChunk() : Int {
        return Solution().sizeOfTheLargestIceChunk()
    }
}

fun main() {
    val (n, q) = readln().split(Regex("\\s+")).map { it.toShort() }

    val grid = Grid(n.toByte(), q)

    println(grid.sumOfIce())
    println(grid.sizeOfTheLargestIceChunk())
}
