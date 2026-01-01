package org.example.p20061

/**
 * 문제 출처 : https://www.acmicpc.net/problem/20061
 *
 * @author
 *  조홍제 : https://blog.naver.com/hjj5612
 */

const val ONE : Byte = 1
const val TWO: Byte = 2
const val THREE: Byte = 3

/**
 * 격자판 위의 위치를 나타내는 클래스
 *
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(var row : Byte, var column : Byte) {
    constructor(row : Int, column : Byte) : this(row.toByte(), column)
    constructor(row : Byte, column : Int) : this(row, column.toByte())
}

/**
 * 격자판 위에 놓이는 블록을 표현하는 클래스
 *
 * @param
 *  t = 1: 크기가 1×1인 블록을 (x, y)에 놓은 경우
 *  t = 2: 크기가 1×2인 블록을 (x, y), (x, y+1)에 놓은 경우
 *  t = 3: 크기가 2×1인 블록을 (x, y), (x+1, y)에 놓은 경우
 */
class Block(t : Byte, x : Byte, y : Byte) {
    val firstTile = Location(x, y)
    val secondTile : Location?

    init {
        when(t) {
            ONE -> { secondTile = null }
            TWO -> { secondTile = Location(x, y + 1)}
            THREE -> { secondTile = Location(x + 1, y) }
            else -> throw IllegalArgumentException("타일의 종류에 문제가 있습니다. 타일의 종류를 나타내는 숫자는 1~3 까지 입니다.")
        }
    }

    fun rotate90() {
        var t : Byte = firstTile.row
        firstTile.row = firstTile.column
        firstTile.column = t

        if(secondTile != null) {
            t = secondTile.row
            secondTile.row = secondTile.column
            secondTile.column = t
        }
    }
}


/**
 * 블럭들이 놓이는 격자판
 *
 * 문제에서는 격자판이 세 종류인데,
 * 이 격자판은 녹색 보드 판과 파란색 보드 판을 표현한다.
 * 파란색 보드를 시계방향으로 90도 회전하면 녹색 보드와 같은 모양이 되기 때문에
 * 이 클래스 하나로 두가지 모두를 표현한다.
 */
class Grid {
    companion object {
        const val rowSize = 6       // 행의 크기
        const val columnSize = 4    // 열의 크기
    }

    //격자 배열
    private val grid = Array(rowSize) { BooleanArray(columnSize) { false } }        // false 인 이유는 처음에는 빈 공간이기 때문.

    /**
     * 단순하게 격자판을 한칸 아래로 스크롤한다.
     */
    private fun scrollDown() {
        for(i in (rowSize - 1) downTo 1) {
            grid[i] = grid[i - 1]
        }
        grid[0] = BooleanArray(columnSize) { false }
    }

    /**
     * 한 줄을 지우고, 지운 줄 위의 블럭들을 한 칸 아래로 스크롤한다.
     *
     * @param
     *  row : 행의 번호
     */
    private fun eraseRowAndScrollDown(row : Int) {
        for(i in row downTo 1) {
            grid[i] = grid[i - 1]
        }
        grid[0] = BooleanArray(columnSize) { false }
    }

    /**
     * 블럭을 격자판 위에 내려 놓습니다.
     */
    fun putDown(block : Block) {
        var putItDown = false       // 블록은 내려 놓지 않았음

        if(block.secondTile != null) {      // 블록에 타일이 두개 이면
            if (block.firstTile.row == block.secondTile.row) {
                val col1 = block.firstTile.column
                val col2 = block.secondTile.column

                for(row in 0 until rowSize) {
                    if(grid[row][col1.toInt()] || grid[row][col2.toInt()]) {
                        grid[row - 1][col1.toInt()] = true
                        grid[row - 1][col2.toInt()] = true
                        putItDown = true
                        break
                    }
                }

                if(!putItDown) {        // 아직 내려 놓지 않았다면
                    grid[rowSize - 1][col1.toInt()] = true
                    grid[rowSize - 1][col2.toInt()] = true
                }
            } else {
                require(block.firstTile.column == block.secondTile.column) { "열 번호가 같아야 한다." }

                val col = block.firstTile.column
                for(row in 0 until rowSize) {
                    if(grid[row][col.toInt()]) {
                        grid[row - 1][col.toInt()] = true
                        grid[row - 2][col.toInt()] = true
                        putItDown = true
                        break
                    }
                }
                if(!putItDown) {        // 아직 내려 놓지 않았다면
                    grid[rowSize - 1][col.toInt()] = true
                    grid[rowSize - 2][col.toInt()] = true
                }
            }
        } else {    // 블럭에 타일이 하나 인 경우
            val col = block.firstTile.column
            for(row in 0 until rowSize) {
                if(grid[row][col.toInt()]) {
                    grid[row - 1][col.toInt()] = true
                    putItDown = true
                    break
                }
            }

            if(!putItDown) {    // 아직 내려 놓지 않았다면
                grid[rowSize - 1][col.toInt()] = true
            }
        }
    }

    /**
     * @return
     *  격자판에 있는 타일의 개수를 반환합니다.
     */
    fun howManyTiles() : Int {
        var answer = 0

        for(i in 0 until rowSize) {
            for(j in 0 until columnSize) {
                if(grid[i][j]) answer++
            }
        }
        return answer
    }

    /**
     * @return
     *  타일이 임의의 행에 가득차면 참을 반환합니다.
     *
     * @param
     *  row : 행 번호 (2~5)
     */
    private fun areTheTilesFull(row : Int) : Boolean {
        return grid[row].all { it }
    }

    /**
     * @return
     *  0 번 행에 타일이 있으면 참을 반환한다.
     */
    private fun isThereATileInRow0() : Boolean {
        return grid[0].any { it }
    }

    /**
     * @return
     *  1 번 행에 타일이 있으면 참을 반환합다.
     */
    private fun isThereATileInRow1() : Boolean {
        return grid[1].any { it }
    }

    /**
     * 격자를 정상적으로 동작 시킵니다.
     *
     * @return
     *  한 번의 동작으로 얻을 수 있는 점수를 반환합니다.
     */
    fun operate() : Int {
        var row = rowSize - 1   // 마지막 줄
        var score = 0           // 점수

        while(row > 1) {
            if(areTheTilesFull(row)) {
                eraseRowAndScrollDown(row)
                score++
            } else row--
        }

        val b1 = isThereATileInRow0()
        val b2 = isThereATileInRow1()

        if(b1) scrollDown()
        if(b2) scrollDown()

        return score
    }
}

fun main() {
    var score = 0           // 점수
    val greenGrid = Grid()  // 녹색 격자판
    val blueGrid = Grid()   // 파란색 격자판

    val n = readln().toInt()    // 블록을 놓은 횟 수
    repeat(n) {
        val (t, x, y) = readln().split(Regex("\\s+")).map { it.toByte() }
        val block = Block(t, x, y)
        greenGrid.putDown(block)        // 블럭을 놓는다.
        score += greenGrid.operate()    // 격자판이 움직인다.
        block.rotate90()                // 블럭을 90도 회전시킨다.
        blueGrid.putDown(block)         // 블럭을 놓는다.
        score += blueGrid.operate()     // 격자판이 움직인다.
    }

    println(score)
    println(greenGrid.howManyTiles() + blueGrid.howManyTiles())
}
