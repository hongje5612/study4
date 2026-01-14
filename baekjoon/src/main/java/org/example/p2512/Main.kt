package org.example.p2512

/**
 * 문제 출처 : https://www.acmicpc.net/problem/2512
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

fun main() {
    readln().toInt()    // 지방의 수
    val budgetRequest = readln().split(" ").map { it.toInt() }.toIntArray()     // 예산 요청
    val totalBudget = readln().toInt()      // 총 예산

    println("${Solution(budgetRequest, totalBudget).simulate()}")
}

class Solution(private val budgetRequest : IntArray, private val totalBudget : Int) {
    /**
     * @return
     *  예산이 통과 될 수 있다면 참을 반환한다.
     */
    private fun canTheBudgetPass() : Boolean {
        return budgetRequest.sum() <= totalBudget
    }

    /**
     * @return
     *  상한액 보다 많은 예산은 상한액으로 해서 예산이 통과 될 수 있으면 참을 반환한다.
     *
     * @param
     *  upperLimit : 상한액
     */
    private fun canTheBudgetPass(upperLimit : Int): Boolean {
        val acc = budgetRequest.sumOf { budget ->
            if(budget > upperLimit) upperLimit else budget
        }

        return acc <= totalBudget
    }

    /**
     * 이분 탐색
     *
     * @return
     *  상한액을 반환합니다.
     *
     * @param
     *  from    : 상한액의 하한값
     *  to      : 상한액의 상한값
     */
    private fun solve(from : Int, to : Int) : Int {
        if(to - from <= 3) {
            for(limit in to downTo from) {
                if(canTheBudgetPass(limit)) return limit
            }
            throw IllegalStateException("상한액을 찾지 못했습니다.")
        }
        else {
            val middleUpperLimit = (to + from) / 2
            if(canTheBudgetPass(middleUpperLimit)) return solve(middleUpperLimit, to)
            else return solve(from , middleUpperLimit - 1)
        }
    }

    fun simulate() : Int {
        if(canTheBudgetPass()) return budgetRequest.max()
        else return solve(0, totalBudget)
    }
}
