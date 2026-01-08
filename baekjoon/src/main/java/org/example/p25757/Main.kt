package org.example.p25757

/**
 * 문제 출처 : https://www.acmicpc.net/problem/25757
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

/*
플레이할 미니게임으로는 윷놀이, 같은 그림 찾기, 원카드가 있습니다.
각각 2, 3, 4 명이서 플레이하는 게임이며 인원수가 부족하면 게임을 시작할 수 없습니다
 */

const val Y = 2     // 윷놀이이는 2 명이 한다.
const val F = 3     // 틀린 그림 찾기는 3 명이 한다.
const val O = 4     // 원 카드는 4 명이 한다.

fun main() {
    val (n, kind) = readln().split(" ")     // n : 임스와 같이 플레이하기를 신청한 횟수, kind : 같이 플레이할 게임의 종류가
    val players = HashSet<String>()
    repeat(n.toInt()) {
        players.add(readln())
    }

    when(kind) {
        "Y" -> { println(players.size) }
        "F" -> { println(players.size / (F - 1)) }
        "O" -> { println(players.size / (O - 1)) }
        else -> { throw IllegalArgumentException() }
    }
}
