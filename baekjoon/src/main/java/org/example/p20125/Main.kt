package org.example.p20125

import java.util.Optional
import kotlin.reflect.KFunction1

/**
 * 문제 출처 : https://www.acmicpc.net/problem/20125
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

const val BODY = '*'    // *는 쿠키의 신체 부분

/**
 * 위치는 나타내는 클래스
 *
 * @property
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Short, val col : Short) {
    constructor(row : Int, col : Int) : this(row.toShort(), col.toShort())
    constructor(row : Short, col : Int) : this(row, col.toShort())
    constructor(row : Int, col : Short) : this(row.toShort(), col)

    fun left() : Location = Location(row, col - 1)
    fun right() : Location = Location(row, col + 1)
    fun up() : Location = Location(row - 1, col)
    fun down() : Location = Location(row + 1, col)

    override fun toString(): String {
        return "${row + 1} ${col + 1}"
    }
}

/**
 * 문제를 해결하는 클래스
 *
 * @property
 *  square : 쿠키가 누워있는 정사각형
 */
class Solution(val square : Array<CharArray>) {

    fun Location.isValid() : Boolean = (row in 0 until square.size) && (col in 0 until square.size)

    fun searchHead() : Optional<Location> {
        for(row in 0 until square.size) {
            for(col in 0 until square.size) {
                if(square[row][col] == BODY) return Optional.of(Location(row, col))
            }
        }

        return Optional.empty()
    }

    /**
     * 팔과 다리의 길이를 계산한다.
     *
     * @param
     *  startLocation : 신체 부위가 시작되기 직전의 위치,
     *                  팔 길이와 허리의 길이를 계산할 때는 심장의 위치
     *                  다리의 길이를 계산할 때는 허리의 끝 왼쪽, 허리의 끝 오른쪽
     *  nextLocationFunction : Location::left, Location::right, Location::up, Location::down 중 하나
     *
     * @return
     *  pair.first : 길이
     *  pair.second : 그 신쳉 부의의 마지막 위치, 팔의 끝, 허리의 끝
     */
    private fun calculateLength(startLocation : Location, nextLocationFunction : KFunction1<Location, Location>) : Pair<Short, Location?> {
        var answer : Short = 0 // 왼 팔의 길이
        var currentLocation = startLocation
        var endLocation : Location? = null

        while(true) {
            currentLocation = nextLocationFunction.invoke(currentLocation)
            if(!currentLocation.isValid()) break
            if(square[currentLocation.row.toInt()][currentLocation.col.toInt()] == BODY) {
                answer++
                endLocation = currentLocation
            }
            else break
        }
        return Pair(answer, endLocation)
    }

    fun printAnswer() {
        var locationOfHead = Location(0, 0)
        searchHead().ifPresent { locationOfHead = it }
        val locationOfHeart = locationOfHead.down()

        println(locationOfHeart)

        val leftArm = calculateLength(locationOfHeart, Location::left)
        val rightArm = calculateLength(locationOfHeart, Location::right)
        val waist = calculateLength(locationOfHeart, Location::down)
        val leftLeg = calculateLength(waist.second!!.left(), Location::down)
        val rightLeg = calculateLength(waist.second!!.right(), Location::down)

        println("${leftArm.first} ${rightArm.first} ${waist.first} ${leftLeg.first} ${rightLeg.first}")
    }
}

fun main() {
    val n = readln().toShort()      // 쿠키가 누워있는 정사각형의 한 변의 크기
    val square = Array(n.toInt()) {
        readln().toCharArray()
    }

    Solution(square).printAnswer()
}
