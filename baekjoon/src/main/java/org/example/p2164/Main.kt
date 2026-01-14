package org.example.p2164

/**
 * 문제 출처 : https://www.acmicpc.net/problem/2164
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

fun main() {
    val n = readln().toInt()
    solve(n)
}

fun solve(n: Int) {
    val queue = ArrayDeque<Int>()

    for(i in 1..n) queue.add(i)

    if(queue.size == 1) {
        println(queue.first())
        return
    }

    while(true) {
        queue.removeFirst()
        if(queue.size == 1) {
            println(queue.first())
            return
        }
        val t = queue.removeFirst()
        queue.addLast(t)
    }
}