package org.example.p17140.try01

/**
 * 출처 : https://www.acmicpc.net/problem/17140
 *
 * @author
 *  조홍제
 */

const val ZERO : Byte = 0
/**
 * 배열 A 틀래스
 *
 * @param
 *  arrayA : 최초의 3x3열의 이차원 배열
 */
class ArrayA(var arrayA : Array<ByteArray>) {
    /**
     * 정렬을 할 때 사용된다.
     *
     * @param
     *  number  : 배열 안에 있는 숫자
     *  count   : 숫자가 등장한 횟 수
     */
    class Element(val number : Byte, var count : Byte) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Element

            return number == other.number
        }

        override fun hashCode(): Int {
            return number.toInt()
        }
    }

    private fun sortSet(set : Set<Element>) : List<Element> {
        return set.toMutableList().sortedWith { element1, element2 ->
            if(element1.count > element2.count) return@sortedWith 1
            else if(element1.count == element2.count) {
                if(element1.number > element2.number) return@sortedWith 1
                else if(element1.number == element2.number) return@sortedWith 0
                else return@sortedWith -1
            } else return@sortedWith -1
        }.take(50)
    }

    /**
     * R 연산: 배열 A의 모든 행에 대해서 정렬을 수행한다. 행의 개수 ≥ 열의 개수인 경우에 적용된다.
     *
     * 한 행 또는 열에 있는 수를 정렬하려면, 각각의 수가 몇 번 나왔는지 알아야 한다.
     * 그 다음, 수의 등장 횟수가 커지는 순으로, 그러한 것이 여러가지면 수가 커지는 순으로 정렬한다.
     * 그 다음에는 배열 A에 정렬된 결과를 다시 넣어야 한다.
     * 정렬된 결과를 배열에 넣을 때는, 수와 등장 횟수를 모두 넣으며, 순서는 수가 먼저이다.
     */
    private fun rOperate() {
        fun rOperate(row : Int) : List<Element> {
            val set = HashSet<Element>()
            for(col in 0..arrayA[row].lastIndex) {
                val number = arrayA[row][col]
                if(number == ZERO) continue
                val element = set.firstOrNull { it.number == number }
                if(element == null) set.add(Element(number, 1))
                else element.count++
            }

            return sortSet(set)
        }

        val t = Array(arrayA.size) {
            rOperate(it)
        }

        var max = 0
        for(list in t) {
            if(list.size > max) max = list.size
        }

        val tArray = Array(arrayA.size) {
            ByteArray(max * 2) { 0 }
        }

        var row = 0
        for(list in t) {
            var col = 0
            for(element in list) {
                tArray[row][col] = element.number
                tArray[row][col + 1] = element.count
                col += 2
            }
            row++
        }

        arrayA = tArray
    }

    /**
     * C 연산: 배열 A의 모든 열에 대해서 정렬을 수행한다. 행의 개수 < 열의 개수인 경우에 적용된다.
     *
     * 한 행 또는 열에 있는 수를 정렬하려면, 각각의 수가 몇 번 나왔는지 알아야 한다.
     * 그 다음, 수의 등장 횟수가 커지는 순으로, 그러한 것이 여러가지면 수가 커지는 순으로 정렬한다.
     * 그 다음에는 배열 A에 정렬된 결과를 다시 넣어야 한다.
     * 정렬된 결과를 배열에 넣을 때는, 수와 등장 횟수를 모두 넣으며, 순서는 수가 먼저이다.
     */
    private fun cOperate() {
        fun cOperate(col : Int) : List<Element> {
            val set = HashSet<Element>()
            for(row in 0 until arrayA.size) {
                val number = arrayA[row][col]
                if(number == ZERO) continue
                val element = set.firstOrNull { it.number == number }
                if(element == null) set.add(Element(number, 1))
                else element.count++
            }

            return sortSet(set)
        }

        val t = Array(arrayA[0].size) {
            cOperate(it)
        }

        var max = 0
        for(list in t) {
            if(list.size > max) max = list.size
        }

        val tArrayA = Array(max * 2) {
            ByteArray(arrayA[0].size) { 0 }
        }

        var col = 0
        for(list in t) {
            var row = 0
            for(element in list) {
                tArrayA[row][col] = element.number
                tArrayA[row + 1][col] = element.count
                row += 2
            }
            col++
        }

        arrayA = tArrayA
    }

    fun operate() {
        if(arrayA.size >= arrayA[0].size) rOperate() else cOperate()
    }

    fun getNumber(r : Int, c : Int) : Byte = arrayA[r][c]
}

/**
 * 문제를 푸는 함수
 *
 * @param
 *  r : 행 번호
 *  c : 열 번호
 *  k : (r, c) 위치의 값이 k가 되어야 함
 *  arrayA : 최초의 3x3 인 이차원 배열
 *
 * @return
 *  정답을 반환합니다.
 */
fun solve(r : Byte, c : Byte, k : Byte, arrayA : Array<ByteArray>) : Int {
    val arr = ArrayA(arrayA)
    var t = 0                   // 현제시간을 나타내는 변수
    val limitTime = 100         //제한시간
    var number : Byte           // 배열에서 읽어 들인 값
    val row = r - 1             // 문제에서는 행의 번호를 1부터 시작하기 때문에 1를 빼준다.
    val col = c - 1

    while(t <= limitTime) {
        try {
            number = arr.getNumber(row, col)
        } catch (e : Exception) {
            number = ZERO       // 문제에서는 number 가 자연수라고 했기 때문에 ZERO 이면 k와 비교할 때 항상 거짓이 됨
        }

        if(number == k) return t
        arr.operate()
        ++t
    }
    return -1
}

fun main() {
    val (r, c, k) = readln().split(Regex("\\s+")).map { it.toByte() }
    val arrayA = Array(3) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }

    println(solve(r, c, k, arrayA))
}
