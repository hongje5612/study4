package org.example.p9017

/**
 * 문제 출처 : https://www.acmicpc.net/problem/9017
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

const val CONSTRAINTS = 6       // 6명 이상 일 것

fun main() {
    val t = readln().toInt()        // 테스트 케이스 수
    val problems = ArrayList<List<Short>>()
    repeat(t) {
        val n = readln().toInt()
        val problem = readln().split(" ").map { it.toShort() }
        problems.add(problem)
    }

    for(problem in problems) {
        println("${Solution(problem).findTheWinningTeam().toInt()}")
    }
}

class Solution(private val list : List<Short>) {

    /*
        key         : 팀 번호
        value       : 팀이 받은 점수들
     */
    private val records = HashMap<Short, MutableList<Int>>()

    /**
     * @return
     *  참가자가 6명 미만인 팀을 반환한다.
     */
    private fun teamsWithLessThan6Participants() : Set<Short> {
        /*
            key     : 팀 번호
            value   : 팀에서 출전한 선수의 수
         */
        val countMap = HashMap<Short, Int>()

        for(team in list) {
            val count = countMap[team]
            if(count != null) countMap[team] = count + 1 else countMap[team] = 1
        }

        val set = mutableSetOf<Short>()
        for((team, count) in countMap) {
            if(count < CONSTRAINTS) set.add(team)
        }

        return set
    }

    init {
        val excludedTeams = teamsWithLessThan6Participants()

        var score = 1
        for(team in list) {
            if(excludedTeams.contains(team)) continue

            val scores = records[team]
            if(scores != null) {
                scores.add(score)
            }
            else {
                val l = mutableListOf<Int>()
                l.add(score)
                records[team] = l
            }
            score++
        }

        for((_, scores) in records) {
            scores.sort()
        }
    }

    /**
     * @return
     *  선두 주자 4명의 점수의 합을 반환한다.
     */
    private fun sum(list: MutableList<Int>): Int {
        var result = 0
        var index = 0

        for(score in list) {
            result += score
            if(++index > 3) break       // 좋은 점수를 받은 4명만 점수 만 계산한다.
        }

        return result
    }

    /**
     * @return
     *  우승팀을 반환합니다.
     */
    fun findTheWinningTeam() : Short {
        val map = records.toSortedMap { b1, b2 ->
            val scores1 = records[b1] ?: throw IllegalStateException("팀의 점수가 존재하지 않습니다.")
            val scores2 = records[b2] ?: throw IllegalStateException("팀의 점수가 존재하지 않습니다.")

            val sum1 = sum(scores1)
            val sum2 = sum(scores2)

            if(sum1 > sum2) return@toSortedMap 1
            else if(sum1 == sum2) {
                val score1 = scores1[4]
                val score2 = scores2[4]
                if(score1 > score2) return@toSortedMap 1
                else if(score1 == score2) return@toSortedMap 0
                else return@toSortedMap -1
            } else return@toSortedMap -1
        }
        return map.firstKey()
    }
}
