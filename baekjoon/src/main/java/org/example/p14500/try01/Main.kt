package org.example.p14500.try01

/**
 * 출처 : https://www.acmicpc.net/problem/14500
 * @author : 조홍제
 */

/**
 * 하나의 테트리미노는 2차원 배열로 표현한다.
 * 테트리미노의 행 크기와 열 크기는 테트리미노를 꽉 채은 배열의 크기이다.
 */
interface Tetromino {
    companion object {
        const val FILL = true       // 배열 중 테트리미노의 블럭이 있는 곳은 참
        const val EMPTY = false     // 배열 중 테트리미노의 블럭이 없는 곳은 거짓
    }

    var blocks : Array<BooleanArray>    // 테트로미노의 정보를 담고 있는 변수
    var rowSize : Byte      // 행의 크기
    var colSize : Byte      // 열의 크기

    /**
     * @param
     *  row : 행의 위치
     *  col : 열의 위치
     *
     * @return
     *  파라미트로 전달되는 행의 위치와 열의 위치에 블럭이 있으면 참 그렇지 않으면 거짓을 반환합니다
     */
    fun isFill(row : Byte, col : Byte) : Boolean {
        return blocks[row.toInt()][col.toInt()]
    }

    /**
     * 테트리미노를 90도 시계방향으로 회전시킨다.
     */
    fun rotate() {
        val b = Array(colSize.toInt()) { r ->
            val booleanArray = BooleanArray(rowSize.toInt())
            for(c in 0 until rowSize) {
                booleanArray[c] = blocks[rowSize - 1 - c][r]
            }
            booleanArray
        }
        val t = rowSize
        rowSize = colSize
        colSize = t

        blocks = b
    }

    /**
     * 테트리미노를 대칭시킨다.
     */
    fun symmetry() {
        val TWO : Byte = 2
        if(rowSize == TWO) {
            for(c in 0 until colSize) {
                val t = blocks[0][c]
                blocks[0][c] = blocks[1][c]
                blocks[1][c] = t
            }
        } else if(colSize == TWO) {
            for(r in 0 until rowSize) {
                val t = blocks[r][0]
                blocks[r][0] = blocks[r][1]
                blocks[r][1] = t
            }
        }
    }
}

/**
 * 테트리미노의 모양은 아래와 같다.
 *
 *      +----+----+----+----+
 *      |    |    |    |    |
 *      +----+----+----+----+
 */
class Tetromino1 : Tetromino {
    override var blocks = Array(1) { booleanArrayOf(Tetromino.FILL, Tetromino.FILL, Tetromino.FILL, Tetromino.FILL) }

    override var rowSize: Byte = 1
    override var colSize: Byte = 4
}

/**
 * 테트리미노의 모양은 아래와 같습니다.
 *
 *      +----+----+
 *      |    |    |
 *      +----+----+
 *      |    |    |
 *      +----+----+
 */
class Tetromino2 : Tetromino {
    override var blocks: Array<BooleanArray> = Array(2) {
        BooleanArray(2) { Tetromino.FILL }
    }
    override var rowSize: Byte = 2
    override var colSize: Byte = 2
}

/**
 * 테느리미노의 모양은 아래와 같습니다.
 *
 *      +----+
 *      |    |
 *      +----+
 *      |    |
 *      +----+----+
 *      |    |    |
 *      +----+----+
 */
class Tetromino3 : Tetromino {
    override var blocks: Array<BooleanArray> = Array(3) {
        BooleanArray(2) { Tetromino.FILL }
    }
    override var rowSize: Byte = 3
    override var colSize: Byte = 2

    init {
        blocks[0][1] = Tetromino.EMPTY
        blocks[1][1] = Tetromino.EMPTY
    }
}

/**
 * 테트리미노의 모양은 아래와 같습니다.
 *
 *      +----+
 *      |    |
 *      +----+----+
 *      |    |    |
 *      +----+----+
 *           |    |
 *           +----+
 */
class Tetromino4 : Tetromino {
    override var blocks: Array<BooleanArray> = Array(3) {
        BooleanArray(2) { Tetromino.FILL }
    }
    override var rowSize: Byte = 3
    override var colSize: Byte = 2

    init {
        blocks[0][1] = Tetromino.EMPTY
        blocks[2][0] = Tetromino.EMPTY
    }
}

/**
 * 테트리미노의 모양은 아래와 같습니다.
 *
 *      +----+----+----+
 *      |    |    |    |
 *      +----+----+----+
 *           |    |
 *           +----+
 */
class Tetromino5 : Tetromino {
    override var blocks: Array<BooleanArray> = Array(2) {
        BooleanArray(3) { Tetromino.FILL }
    }
    override var rowSize: Byte = 2
    override var colSize: Byte = 3

    init {
        blocks[1][0] = Tetromino.EMPTY
        blocks[1][2] = Tetromino.EMPTY
    }
}

/**
 * 문제를 시뮬레이션해서 결과를 구하는 클래스
 *
 * @param
 *  rowSize : paper 의 행의 크기
 *  colSize : paper 의 열의 크기
 *  paper   : 종이 위에 한 칸씩 구분된 칸에 숫자가 하나씩 적혀있는 2차원 배열
 */
class Solution(val rowSize : Short, val colSize : Short, val paper : Array<ShortArray>) {
    private val answer : Int // 문제의 정답을 담는 변수

    /**
     * @param
     *  row : 행의 위치
     *  col : 열의 위치
     *
     * @return
     *  종이 위의 위치인 경우 참, 그렇지 않고 밖으로 나가는 경우 거짓을 반환합니다.
     */
    private fun isValidLocation(row : Int, col : Int) : Boolean = (row in 0 until rowSize) && (col in 0 until colSize)

    /**
     * 테트로미노를 Paper 위의 (row, col) 위치에 놓아 본다.
     *
     * @param
     *  row : 행의 위치
     *  col : 열의 위치
     *
     * @return
     *  테트로미노의 블럭들의 놓았을 경우, 놓은 위치에 있는 점수들의 합을 반환합니다.
     *  놓을 수 없을 경우 -1을 반환합니다.
     */
    private fun Tetromino.placeOnPaper(row : Short, col : Short) : Int {
        var sum = 0

        for(r in 0 until this.rowSize) {
            for(c in 0 until this.colSize) {
                val y = row + r
                val x = col + c
                if(!isValidLocation(y, x)) return -1

                if(isFill(r.toByte(), c.toByte())) {
                    sum += paper[y][x]
                }
            }
        }
        return sum
    }

    /**
     * 문제에서 제시하는 해답을 계산합니다.
     *
     * @return
     *  다섯 개의 테트로미노 중 하나를 종이 위에 놓았을 때, 종이 위의 숫자들의 합의 최대값을 반환합니다.
     */
    private fun calculate() : Int {
        var max = 0
        val tetrominoes = arrayOf(Tetromino1(), Tetromino2(), Tetromino3(), Tetromino4(), Tetromino5())

        /**
         * @param
         *  테트로미노
         * @return
         *  인수로 주어지 테트로미노를 임의의 위치에 놓았을 때, 종이 위의 숫자들의 합의 최대값을 반환합니다.
         */
        fun findMaxScore(tetromino: Tetromino) : Int {
            var max = 0
            for(i in 0..3) {
                for(j in 0..1) {
                    for(row in 0..rowSize - tetromino.rowSize) {
                        for(col in 0..colSize - tetromino.colSize) {
                            val t = tetromino.placeOnPaper(row.toShort(), col.toShort())
                            if(t > max) max = t
                        }
                    }
                    tetromino.symmetry()
                }
                tetromino.rotate()
            }
            return max
        }

        for(tetromino in tetrominoes) {
            val t = findMaxScore(tetromino)
            if(t > max) max = t
        }

        return max
    }

    init {
        answer = calculate()
    }

    fun getAnswer() : Int = answer
}

fun main() {
    val (n, m) = readln().split(Regex("\\s+")).map { it.toShort() }
    val paper = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toShort() }.toShortArray()
    }

    println("${Solution(n, m, paper).getAnswer()}")
}
