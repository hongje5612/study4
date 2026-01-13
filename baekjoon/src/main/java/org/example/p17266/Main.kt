package org.example.p17266

/**
 * 문제 출처 : https://www.acmicpc.net/problem/17266
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

fun main() {
    val n = readln().toInt()        // 굴다리의 길이
    readln()                        // 가로등 개수
    val streetLights = readln().split(" ").map(String::toInt).toIntArray()      // 가로등의 위치
    println("${Solution(n, streetLights).calculateHeight()}")
}

/**
 * 문제를 해결하는 클래스
 *
 * @property
 *  n               : 굴다리의 길이
 *  streetLights    : 가로등의 위치
 */
class Solution(private val n : Int, private val streetLights : IntArray) {

    /**
     * @return
     *  가로등의 높이를 height 로 했을 경우, 굴다리 전체를 가로등으로 비출 수 있으면 참을 반환합니다.
     *
     * @param
     *  height : 가로등의 높이
     */
    private fun meetTheConditions(height : Int) : Boolean {
        var left = 0        // 가로등의 왼쪽 편이 비추어야하는 경계, 가로등의 왼쪽으로 비추는 빛이 left 작거나 같아야 조건을 만족한다.

        for(streetLight in streetLights) {
            val l = streetLight - height
            if(l > left) return false
            left = streetLight + height
        }

        val right = streetLights[streetLights.lastIndex] + height
        return if(right >= n) true else false
    }

    /**
     * @return
     *  굴다리를 모두 비출 수 있는 가로등의 최소 높이를 반환한다.
     */
    fun calculateHeight() : Int {
        fun height(fromHeight : Int, toHeight : Int) : Int {
            if(toHeight - fromHeight <= 3) {
                for(height in fromHeight..toHeight) {
                    if(meetTheConditions(height)) return height
                }
                throw IllegalArgumentException("조건을 만족하는 최소한의 높이를 찾지 못했습니다.")
            } else {
                val middleHeight = (toHeight + fromHeight) / 2
                if(meetTheConditions(middleHeight)) return height(fromHeight, middleHeight)
                else return height(middleHeight + 1, toHeight)
            }
        }

        return height(0, n)
    }
}
