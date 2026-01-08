package org.example.p2816

/**
 * 문제 출처 : https://www.acmicpc.net/problem/2816
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

const val ONE : Byte = 1        // 화살표를 한 칸 아래로 내린다. (채널 i에서 i+1로)
const val TWO : Byte = 2        // 화살표를 위로 한 칸 올린다. (채널 i에서 i-1로)
const val THREE : Byte = 3      // 현재 선택한 채널을 한 칸 아래로 내린다. (채널 i와 i+1의 위치를 바꾼다. 화살표는 i+1을 가리키고 있는다)
const val FOUR: Byte = 4        // 현재 선택한 채널을 위로 한 칸 올린다. (채널 i와 i-1의 위치를 바꾼다. 화살표는 i-1을 가리키고 있다)

const val KBS1 = "KBS1"
const val KBS2 = "KBS2"

class Solution(channels : Array<String>) {
    private var locationOfKBS1 : Byte = Byte.MIN_VALUE  // 현제는 KBS1의 위치를 모른다
    private var locationOfKBS2 : Byte = Byte.MIN_VALUE  // 현제는 KBS2의 위치를 모른다.
    private var arrow : Byte = 0

    init {
        channels.forEachIndexed { i, ch ->
            if(ch == KBS1) locationOfKBS1 = i.toByte()
            if(ch == KBS2) locationOfKBS2 = i.toByte()
        }
    }

    private fun searchKBS1() {
        repeat(locationOfKBS1.toInt()) { print(ONE) }
        arrow = locationOfKBS1
    }

    private fun moveKBS1() {
        if(locationOfKBS2 in 0 until locationOfKBS1) locationOfKBS2++
        repeat(locationOfKBS1.toInt()) { print(FOUR) }
        arrow = 0
    }

    private fun searchKBS2() {
        repeat(locationOfKBS2.toInt()) { print(ONE) }
        arrow = locationOfKBS2
    }

    private fun moveKBS2() {
        repeat(locationOfKBS2.toInt() - 1) { print(FOUR) }
        arrow = 1
    }

    fun move() {
        searchKBS1()
        moveKBS1()
        searchKBS2()
        moveKBS2()
    }
}


fun main() {
    val n = readln().toInt()    // 채널의 수
    val ss = ArrayList<String>()
    repeat(n) {
        ss.add(readln())
    }
    Solution(ss.toTypedArray()).move()
}
