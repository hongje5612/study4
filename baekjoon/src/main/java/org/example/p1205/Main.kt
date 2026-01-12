package org.example.p1205

/**
 * 문제 출처 : https://www.acmicpc.net/problem/1205
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

/**
 * 문제를 해결하는 클래스
 *
 * @property
 *  score   : 태수의 점수
 *  p       : 랭킹 리스트에 올라 갈 수 있는 점수의 개수
 *
 * @param
 *  list    : 점수가 담겨져 있는 리스트
 */
class Solution(val score : Int, val p : Int, list : List<Int>) {
    class Person(val score : Int,val who : Boolean = false)

    private val persons : MutableList<Person> = mutableListOf()

    init {
        for(value in list) {
            persons.add(Person(value))
        }
        persons.add(Person(score, true))      // 점수 리스트에 태수 점수를 넣어준다.
        persons.sortByDescending { it.score }       // 내림차순으로 정렬한다.
    }

    /**
     * 태수의 순위(ranking)을 출력한다.
     */
    fun calculateTheRanking() {
        val list = persons.take(p)      // 점수가 담겨 있는 리스트에서 p 개의 사람을 가져온다.

        if(list.isNotEmpty()) {
            var rank = 1                // 현제 사람의 순위를 나타낸다.
            var sequence = 1            // 사람을 한사람씩 읽을 때 마다 1이 증가한다.
            val it = list.iterator()
            var person = it.next()
            if(person.who) {
                println(rank)
                return
            }
            var score = person.score    // 현제 점수

            while(it.hasNext()) {
                person = it.next()
                sequence++   // 새로운 한 사람을 읽어 보았음
                if(person.score < score) {
                    rank = sequence
                    score = person.score
                } else if(person.score == score) {
                    // 순위(rank)가 변하지 않는다.
                } else throw IllegalStateException("정렬이 되지 않았습니다.")

                if(person.who) {
                    println(rank)
                    return
                }
            }
            println(-1)
        } else {
            throw IllegalStateException("태수의 점수가 입력되지 않았습니다.")
        }
    }
}

fun main() {
    // 리스트에 있는 점수 n개
    // 태수의 점수 score
    // 랭킹 리스트에 올라 갈 수 있는 점수의 개수 p
    val (n, score, p) = readln().split(" ").map { it.toInt() }
    val list : List<Int>
    if(n > 0) {
        list = readln().split(" ").map { it.toInt() }
    } else {
        list = listOf()
    }

    Solution(score, p,list).calculateTheRanking()
}
