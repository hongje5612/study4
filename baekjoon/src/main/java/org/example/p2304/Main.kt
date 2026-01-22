package org.example.p2304

/**
 * 문제 출처 : https://www.acmicpc.net/problem/2304
 *
 * @author
 *  조홍제 -> https://blog.naver.com/hjj5612
 */

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * 막대 기둥
 *
 * @property
 *  location    : 막대 기둥의 위치
 *  height      : 막대 기둥의 높이
 */
class BarPillar(val location : Short, val height : Short)

fun main() {
    val reader = BufferedReader(InputStreamReader(System.`in`))
    val writer = BufferedWriter(OutputStreamWriter(System.`out`))

    // 첫 줄에는 기둥의 개수를 나타내는 정수 N이 주어진다. N은 1 이상 1,000 이하이다
    val n = reader.readLine().toShort()
    val barPillars = ArrayList<BarPillar>()
    repeat(n.toInt()) {
        val (l, h) = reader.readLine().split(" ").map { it.toShort() }
        barPillars.add(BarPillar(l, h))
    }

    writer.write("${solve(barPillars)}\n")

    reader.close()
    writer.close()
}

/**
 * 문제 풀이 방법
 *
 *              +-+
 *              | |
 *              | |
 *            +-+ +-+
 *            | | | |
 *            | | | |
 *          +-+ | | |
 *          | | | | +-+
 *          | | | | | |
 *          | | | | | +-+
 *          | | | | | | |
 *      ---------------------------------------
 *  가장 높은 막대 기둥을 기준으로 좌측과 우측을 나누어 생각한다.
 *  가장 왼쪽편에서 시작하여 오른쪽으로 가면서 높은 막대 기둥이 나타날 때 마다 면적을 계산한다.
 *  가장 오른쪽에서 시작하여 왼쪽으로 가면서 높은 막대 기둥이 나타날 때 마다 면적을 계산하다.
 *  이 두 면적을 모두 합한 후 가장 높은 막대 기둥의 높이를 합하면 정답이 된다.
 */
fun solve(barPillars : ArrayList<BarPillar>) : Long {
    if(barPillars.isEmpty()) throw IllegalArgumentException("No Poles")

    if(barPillars.size == 1) return barPillars[0].height.toLong()

    barPillars.sortBy { it.location }

    var answer = 0L      // 문제의 정답
    val it = barPillars.iterator()
    var barPillar = it.next()
    var leftLocation = barPillar.location
    var leftHeight = barPillar.height

    while(it.hasNext()) {
        val pole = it.next()
        if(pole.height >= leftHeight) {
            answer += (pole.location - leftLocation) * leftHeight.toInt()
            leftLocation = pole.location
            leftHeight = pole.height
        }
    }

    val it2 = barPillars.listIterator(barPillars.size)
    barPillar = it2.previous()
    var rightLocation = barPillar.location
    var rightHeight = barPillar.height

    while(it2.hasPrevious()) {
        val pole = it2.previous()
        if(pole.height > rightHeight) {
            answer += (rightLocation - pole.location) * rightHeight.toInt()
            rightLocation = pole.location
            rightHeight = pole.height
        }
    }

    return answer + leftHeight
}
