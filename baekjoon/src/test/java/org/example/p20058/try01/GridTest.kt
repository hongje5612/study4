package org.example.p20058.try01

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.pow

class Grid {
    private val size = 8

    // 얼음의 양의 정보를 담고 있는 2차원 배열
    private val board : Array<ByteArray> = Array(8) { row -> ByteArray(8) { col -> (row * 8 + col + 1).toByte() } }

    private fun rotateClockwise(row1 : Int, col1 : Int, row2 : Int, col2 : Int) {
        val r1 = row1
        val c1 = col1
        val r2 = row1
        val c2 = col2
        val r3 = row2
        val c3 = col2
        val r4 = row2
        val c4 = col1
        for (i in 0..(row2 - row1) - 1) {
            val t = board[r1][c1 + i]

            board[r1][c1 + i] = board[r4 - i][c4]
            board[r4 - i][c4] = board[r3][c3 - i]
            board[r3][c3 - i] = board[r2 + i][c2]
            board[r2 + i][c2] = t
        }
    }

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

    private fun rotateTheWholeThingClockwise(l : Byte) {
        val widthOrHeight = 2.toDouble().pow(l.toInt()).toInt()

        for(r in 0 until size step widthOrHeight) {
            for(c in 0 until size step widthOrHeight) {
                rotateTheWholeThingClockwise(r, c, widthOrHeight)
            }
        }
    }

    fun show() {
        for(row in 0 until size) {
            for(col in 0 until size) {
                print("${board[row][col]} ")
            }
            println()
        }
    }

    init {
        show()
        println()

        rotateTheWholeThingClockwise(2)
        show()
        println()
    }
}

class GridTest {
    @Test
    fun doesTheRotationWorkProperly() {
        Grid()
    }
}


