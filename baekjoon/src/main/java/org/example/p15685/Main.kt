package org.example.p15685

import java.util.LinkedList

/**
 *  문제 출처 :  https://www.acmicpc.net/problem/15685
 *
 *  @author
 *   조홍제 (https://blog.naver.com/hjj5612)
 */

const val SIZE = 101        // 격자의 크기

val grid = Array(SIZE) { BooleanArray(SIZE) { false} }      // 격자

/**
 * 격자 위의 위치를 나타내는 클래스
 *
 * @param
 *  x : x 좌표
 *  y : y 좌표
 */
class Location(val x : Byte, val y : Byte) {
    constructor(x : Int, y : Int) : this(x.toByte(), y.toByte())
    constructor(x : Int, y : Byte) : this(x.toByte(), y)
    constructor(x : Byte, y : Int) : this(x, y.toByte())

    fun right() : Location = Location(x + 1, y)
    fun up() : Location = Location(x, y - 1)
    fun left() : Location = Location(x - 1, y)
    fun down() : Location = Location(x, y + 1)
}

val nextLocationFunction = arrayOf(Location::right, Location::up, Location::left, Location::down)

/**
 * 격자 위에 드래곤 커브를 그린다.
 *
 * @param
 *  x와 y는 드래곤 커브의 시작 위치
 *  d : 시작 방향
 *      (   0: x좌표가 증가하는 방향 (→)
 *          1: y좌표가 감소하는 방향 (↑)
 *          2: x좌표가 감소하는 방향 (←)
 *          3: y좌표가 증가하는 방향 (↓)     )
 *  g : 세대
 */
fun drawDragonCurve(x : Byte, y : Byte, d : Byte, g : Byte) {
    val directions = ArrayList<Byte>()
    directions.add(d)

    for(generation in 1..g) {
        val currentDirections = LinkedList<Byte>()
        currentDirections.addAll(directions)
        val iterator = currentDirections.descendingIterator()
        while(iterator.hasNext()) {
            val t = iterator.next()
            directions.add(((t + 1) % nextLocationFunction.size).toByte())
        }
    }

    grid[y.toInt()][x.toInt()] = true
    var currentLocation = Location(x, y)
    for(d in directions) {
        val nextLocation = nextLocationFunction[d.toInt()].invoke(currentLocation)
        grid[nextLocation.y.toInt()][nextLocation.x.toInt()] = true
        currentLocation = nextLocation
    }
}

/**
 * @return
 *  정답을 반환한다. (크기가 1×1인 정사각형의 네 꼭짓점이 모두 드래곤 커브의 일부인 것의 개수)
 */
fun count() : Int {
    var retValue = 0
    for(row in 0 until SIZE - 1) {
        for(col in 0 until SIZE - 1) {
            if(grid[row][col] && grid[row][col + 1] && grid[row + 1][col] && grid[row + 1][col + 1]) ++retValue
        }
    }
    return retValue
}

fun main() {
    val n = readln().toByte()       // 드래곤 커브의 개수
    for(i in 0 until n) {
        val (x, y, d, g) = readln().split(Regex("\\s+")).map { it.toByte() }
        drawDragonCurve(x, y, d, g)
    }
    println(count())
}
