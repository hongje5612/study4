package org.example.p21609.try01

/*
    출처 : https://www.acmicpc.net/problem/21609
    제목 : 상어 중학교
 */

/**
 * 격자판의 위에서의 위치를 나타냅니다.
 *
 * @param
 *  row : 열의 위치
 *  col : 행의 위치
 */
class Location(val row : Byte, val col : Byte)

/**
 * 문제에서 이야기 하고 있는 블록
 *
 * @param
 *  color : 색상
 */
class Block(val color : Byte) {
    companion object {
        const val RAINBOW : Byte = 0   // 레인보우 블럭
        const val BLACK : Byte = -1    // 검정 블럭
    }
}

/**
 * 문제에서 이야기 하고 있는 블럭그룹
 *
 * @param
 *  blockLocations : 블럭그룹에 포함된 블러의 위치 정보를 담고 있습니다.
 *  numberOfRainbow : 무지개 블럭의 개수
 *  locationOfReferenceBlock : 기준 블럭의 위치
 *  size : 블럭그룹에 포함된 블럭의 총 개수
 */
class BlockGroup(val blockLocations : Set<Location>, val numberOfRainbow : Short, val locationOfReferenceBlock : Location) {
    val size = blockLocations.size
}

/**
 * 격자
 *
 * @param
 *  size : 격자의 행의 크기 또는 열의 크기
 *  board : 색상 정보를 담고는 2차원 배열
 */
class Grid(val size : Byte, board1 : Array<ByteArray>) {
    private val board = Array(size.toInt()) { row -> Array<Byte?>(size.toInt()) { col -> board1[row][col] } }

    companion object {
        val DX = arrayOf(0, 0, -1, 1)   // 인접하는 상하좌우 거리
        val DY = arrayOf(-1, 1, 0, 0)   // 인접하는 상하좌우 거리
    }

    /**
     * board 2차원 배열을 반시계 방향으로 회전합니다.
     */
    private fun rotateCounterclockwise() {
        /**
         * @param
         * (row1, col1) : 좌 상단 좌표
         * (row2, col2) : 으 하단 좌표
         *
         * 두개의 좌표가 가르치는 사각형 영역의 가장 바같쪽 라인의 값들을 반시계 방향으로 회전합니다.
         */
        fun rotateCounterclockwise(row1 : Int, col1 : Int, row2 : Int, col2 : Int) {
            val r1 = row1
            val c1 = col1

            val r2 = row1
            val c2 = col2

            val r3 = row2
            val c3 = col2

            val r4 = row2
            val c4 = col1

            for(i in 0 until row2 - row1) {
                val t = board[r1][c1 + i]

                board[r1][c1 + i] = board[r2 + i][c2]
                board[r2 + i][c2] = board[r3][c3 - i]
                board[r3][c3 - i] = board[r4 - i][c4]
                board[r4 - i][c4] = t
            }
        }

        var row1 = 0
        var col1 = 0
        var row2 = size - 1
        var col2 = row2

        while(row1 < row2) {
            rotateCounterclockwise(row1, col1, row2, col2)
            row1++
            col1++
            row2--
            col2--
        }
    }

    /**
     * 중력이 작용합니다.
     */
    private fun gravityIsAtWork() {
        /**
         * 주어진 col(열 번호) 열에 중력이 작용하여 블러들이 아래로 떨어진다.
         */
        fun gravityIsAtWork(col : Int) {
            var firstEmptyLocation : Int? = null   // 비어있는 위치를 아직 발견하지 못했음
            var row = size - 1

            while(row >= 0) {
                if(board[row][col] == null) {
                    if(firstEmptyLocation == null) firstEmptyLocation = row
                }
                else {
                    if(board[row][col]!! == Block.BLACK) firstEmptyLocation = null // 비어있는 위치를 아직 발견하지 못했음
                    else if(board[row][col]!!.toInt() >= 0) {
                        if(firstEmptyLocation != null) {
                            board[firstEmptyLocation][col] = board[row][col]
                            board[row][col] = null
                            val r = firstEmptyLocation - 1
                            if (r >= 0 && board[r][col] == null) firstEmptyLocation = r else firstEmptyLocation = null
                        }
                    }
                }
                --row
            }
        }

        for(col in 0 until size) gravityIsAtWork(col)
    }

    fun isValidLocation(row : Int, col : Int) : Boolean = (row in 0 until size) && (col in 0 until size)

    /**
     * bfs (너비 우선 탐색)
     * 인접한 동일한 color를 가지는 일반 블록과 무지개 블록을 모두 방문하여, 블록 그룹을 찾습니다.
     *
     * @param
     *  row : 행의 번호
     *  col : 열의 번호
     *  color : 색상
     */
    private fun bfs(row : Byte, col : Byte, color : Byte) : BlockGroup {
        /*
         * bfs(너비 우선 탐색) 알고리즘에 사용되는 변수
         * 방문한 적이 있으면 참, 그렇지 않은 면 거짓
         */
        val visited = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }
        /*
         * bfs(너비 우선 탐색) 알고리즘에서 사용되는 변수
         * queue 에 담겨 있으면 참 그렇지 않으면 거짓
         */
        val inQueue = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }
        val blockLocations = HashSet<Location>()    // 블럭그룹에 포함된 블럭의 위치 정보를 포함하고 있습니다.
        var numberOfRainbow : Short = 0     // 블럭그룹에 포함된 무지개 블럭의 개수
        var locationOfReferenceBlock : Location = Location(row, col)

        val queue = ArrayDeque<Location>()
        queue.addLast(Location(row, col))
        inQueue[row.toInt()][col.toInt()] = true

        while(queue.isNotEmpty()) {
            val location = queue.removeFirst()

            if(board[location.row.toInt()][location.col.toInt()] != Block.RAINBOW) {
                if(location.row < locationOfReferenceBlock.row) locationOfReferenceBlock = location
                else if(location.row == locationOfReferenceBlock.row) {
                    if(location.col < locationOfReferenceBlock.col) locationOfReferenceBlock = location
                }
            }
            blockLocations.add(location)
            if(board[location.row.toInt()][location.col.toInt()] == Block.RAINBOW) numberOfRainbow++

            inQueue[location.row.toInt()][location.col.toInt()] = false
            visited[location.row.toInt()][location.col.toInt()] = true

            for(index in 0 until DX.size) {
                val r = location.row + DY[index]
                val c = location.col + DX[index]

                if(isValidLocation(r, c) && !inQueue[r][c] && !visited[r][c]) {
                    if(board[r][c] != null && (board[r][c]!! == color || board[r][c]!! == Block.RAINBOW)) {
                        queue.addLast(Location(r.toByte(), c.toByte()))
                        inQueue[r][c] = true
                    }
                }
            }
        }
        return BlockGroup(blockLocations, numberOfRainbow, locationOfReferenceBlock)
    }

    /**
     * 1. 크기가 가장 큰 블록 그룹을 찾는다.
     * 그러한 블록 그룹이 여러 개라면 포함된 무지개 블록의 수가 가장 많은 블록 그룹,
     * 그러한 블록도 여러개라면 기준 블록의 행이 가장 큰 것을,
     * 그 것도 여러개이면 열이 가장 큰 것을 찾는다.
     */
    fun largestBlockGroup() : BlockGroup? {
        val list = ArrayList<BlockGroup>()

        for(row in 0 until size) {
            for (col in 0 until size) {
                if(board[row][col] != null && board[row][col]!! > Block.RAINBOW) {
                    val blockGroup = bfs(row.toByte(), col.toByte(), board[row][col]!!)

                    if(blockGroup.size > 1) list.add(blockGroup)
                }
            }
        }


        list.sortWith { blockGroup1, blockGroup2 ->
            if(blockGroup1.size > blockGroup2.size) return@sortWith 1
            else if(blockGroup1.size == blockGroup2.size) {
                if(blockGroup1.numberOfRainbow > blockGroup2.numberOfRainbow) return@sortWith 1
                else if(blockGroup1.numberOfRainbow == blockGroup2.numberOfRainbow) {
                    if(blockGroup1.locationOfReferenceBlock.row > blockGroup2.locationOfReferenceBlock.row) return@sortWith 1
                    else if(blockGroup1.locationOfReferenceBlock.row == blockGroup2.locationOfReferenceBlock.row) {
                        if(blockGroup1.locationOfReferenceBlock.col > blockGroup2.locationOfReferenceBlock.col) return@sortWith 1
                        else if(blockGroup1.locationOfReferenceBlock.col == blockGroup2.locationOfReferenceBlock.col) return@sortWith 0
                        else return@sortWith -1
                    } else return@sortWith -1
                } else return@sortWith -1
            } else return@sortWith -1
        }

        return list.lastOrNull()
    }

    /**
     * 2. 1에서 찾은 블록 그룹의 모든 블록을 제거한다. 블록 그룹에 포함된 블록의 수를 B라고 했을 때, B^2점을 획득한다.
     */
    fun removeBlocks(blockGroup : BlockGroup) : Int {
        for(location in blockGroup.blockLocations) {
            board[location.row.toInt()][location.col.toInt()] = null
        }

        return blockGroup.size * blockGroup.size
    }

    /**
     *  문제에서 제시한 5가지 과정을 시뮤레이션 한다.
     *
     *  @return
     *   점수의 합을 반환한다.
     */
    fun simulate() : Int {
        var score = 0

        while(true) {
            val blockGroup = largestBlockGroup() ?: break

            score += removeBlocks(blockGroup)

            gravityIsAtWork()

            rotateCounterclockwise()

            gravityIsAtWork()
        }
        return score
    }

    fun show() {
        for(row in 0 until size) {
            for(col in 0 until size) {
                if(board[row][col] != null) System.out.printf("%3d ", board[row][col]) else print("  N ")
            }
            println()
        }
    }
}

fun main() {
    val (n, m) = readln().split(Regex("\\s+")).map { it.toByte() }
    val board = Array(n.toInt()) { readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray() }

    println("${Grid(n, board).simulate()}")
}
