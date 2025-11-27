package org.example.p15684.try02

/**
 * 출처 : https://www.acmicpc.net/problem/15684
 * @author : 조홍제
 */
import java.util.Objects

/**
 * 수평선
 * 가로선의 정보는 두 정수 a과 b로 나타낸다.
 * (1 ≤ a ≤ H, 1 ≤ b ≤ N-1) b번 세로선과 b+1번 세로선을 a번 점선 위치에서 연결했다는 의미이다.
 *
 * @param
 *  a : 수평선을 놓을 수 있는 위치의 번호
 *  b : 수직선 번호
 */
class HorizontalLine(val a : Short, val b : Short) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HorizontalLine

        if (a != other.a) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(a, b)
    }
}


/**
 * 사다리
 *
 * @param
 *  numberOfVerticalLines   : 수직선의 개수
 *  numberOfLocations       : 수평선을 놓을 수 있는 위치의 개수
 */
class Ladder(val numberOfVerticalLines : Short, val numberOfLocations : Short) {
    /**
     * 세로선과 수평선이 올 수 있는 위치가 만나는 교점
     * @param
     *  theNumberOfVerticalLine         : Node를 포함하고 있는 수직선의 번호
     *  theNumberOfLocation             : 수평선이 올 수 있는 위치의 번호
     *  nextNodeInVerticalDirection     : 수직방향으로 다음 노드
     *  nextNodeInHorizontalDirection   : 수평방향으로 다음 노드
     */
    class Node(
        val theNumberOfVerticalLine : Short,
        val theNumberOfLocation : Short,
        var nextNodeInVerticalDirection : Node? = null,
        var nextNodeInHorizontalDirection : Node? = null
    )

    private val rowSize : Int = numberOfLocations + 2   // 출발점과 도착점을 추가해 준다.
    private val colSize = numberOfVerticalLines.toInt()

    // 사다리를 Node의 2차원 배열로 표현해 준다.
    private val nodes = Array(rowSize) { row ->
        Array(colSize) { col ->
            Node(col.toShort(), row.toShort())
        }
    }

    // 수직 방향 노드를 초기화 해 준다.
    init {
        val prev = Array<Node?>(colSize) { null }

        for(row in (rowSize - 1) downTo 0) {
            for(col in 0 until colSize) {
                val node = nodes[row][col]
                node.nextNodeInVerticalDirection = prev[col]
                prev[col] = node
            }
        }
    }

    /**
     * 파라미터로 전달되는 수평선으로 두 수직선을 연결합니다.
     */
    fun connectVerticalLineWith(horizontalLine: HorizontalLine) {
        val b = horizontalLine.b - 1
        val node1 = nodes[horizontalLine.a.toInt()][b]
        val node2 = nodes[horizontalLine.a.toInt()][b + 1]

        node1.nextNodeInHorizontalDirection = node2
        node2.nextNodeInHorizontalDirection = node1
    }

    /**
     * 파라미터로 전달되는 수평선을 제거 합니다.
     */
    fun removeHorizontalLine(horizontalLine: HorizontalLine) {
        val b = horizontalLine.b - 1
        val node1 = nodes[horizontalLine.a.toInt()][b]
        val node2 = nodes[horizontalLine.a.toInt()][b + 1]

        node1.nextNodeInHorizontalDirection = null
        node2.nextNodeInHorizontalDirection = null
    }

    /**
     * @return
     *  파라미터로 주어진 수평선으로 연결되어 있으면 참을 반환합니다.
     */
    fun isItConnectedBy(horizontalLine: HorizontalLine) : Boolean {
        val b = horizontalLine.b - 1
        val node1 = nodes[horizontalLine.a.toInt()][b]
        val node2 = nodes[horizontalLine.a.toInt()][b + 1]

        val con1 = node1.nextNodeInHorizontalDirection === node2
        val con2 = node2.nextNodeInHorizontalDirection === node1

        return con1 && con2
    }

    fun isItDisconnectedBy(horizontalLine: HorizontalLine) : Boolean {
        return !isItConnectedBy(horizontalLine)
    }

    /**
     * @return
     *  문제에서 제시하는 조건(i번 세로선의 결과가 i번이 나와야 한다)이 만족하면 참을 반환합니다.
     */
    fun areTheConditionsMet() : Boolean {
        val destination = (rowSize - 1).toShort()

        fun areTheConditionsMet(startNode : Node) : Boolean {
            var currentNode : Node? = startNode
            val number = startNode.theNumberOfVerticalLine
            var previousNode : Node? = null
            var t : Node? = null

            while(currentNode != null) {
                if(currentNode.theNumberOfLocation == destination) {
                    return currentNode.theNumberOfVerticalLine == number
                }

                t = currentNode

                if(currentNode.nextNodeInHorizontalDirection != null) {
                    if(currentNode.nextNodeInHorizontalDirection !== previousNode) currentNode = currentNode.nextNodeInHorizontalDirection
                    else currentNode = currentNode.nextNodeInVerticalDirection
                } else currentNode = currentNode.nextNodeInVerticalDirection

                previousNode = t
            }
            throw IllegalStateException("문제의 조건을 검사하는 작업을 실패하였습니다.")
        }

        for(col in 0 until colSize) {
            val con = areTheConditionsMet(nodes[0][col])
            if(!con) return false
        }
        return true
    }
}

class Solution(val numberOfVerticalLines: Short, val numberOfLocations: Short, horizontalLines : Set<HorizontalLine>) {
    private val ladder : Ladder = Ladder(numberOfVerticalLines, numberOfLocations)
    private val emptyLocations = HashSet<HorizontalLine>()       // 수평선을 놓을 수 있는 위치이지만, 현제는 수평선이 없는 위치들
    private val answer : Int

    init {
        for(hLine in horizontalLines) ladder.connectVerticalLineWith(hLine)     //최초로 존재하는 수평선을 추가함

        /*
        수평신이 존재할 수 있는 위치 중, 현제 수평선이 없는 위치의 정보를 emptyLocations 변수에 추가함
         */
        for(b in 1 until numberOfVerticalLines) {
            for(a in 1..numberOfLocations) {
                val hLine = HorizontalLine(a.toShort(), b.toShort())
                if(!horizontalLines.contains(hLine)) emptyLocations.add(hLine)
            }
        }

        answer = simulate()
    }

    /**
     * @return
     *  수평선을 놓을 수 있으면 참을 반환합니다.
     */
    private fun HorizontalLine.canBePlaced() : Boolean {
        if(ladder.isItConnectedBy(this)) return false

        val left = (b - 1).toShort()
        val right = (b + 1).toShort()

        val con1 = if(left >= 1) ladder.isItDisconnectedBy(HorizontalLine(a, left)) else true
        val con2 = if(right < numberOfVerticalLines) ladder.isItDisconnectedBy(HorizontalLine(a, right)) else true

        return con1 && con2
    }

    /**
     * @return
     *  수평선을 각각 한개, 두개 또는 세개를 놓아보고 조건이 만족하는지 확인한 후
     *  조건을 만족하면 수평선의 최소 개수를 반환합니다.
     */
    private fun simulate() : Int {
        if(ladder.areTheConditionsMet()) return 0

        var result = 4
        var count = 0

        fun dfs(depth : Int) {
            if(depth == 3) {
                return
            } else {
                var connect = false

                for(horizontalLine in emptyLocations) {
                    if(horizontalLine.canBePlaced()) {
                        ladder.connectVerticalLineWith(horizontalLine)
                        connect = true
                        count++
                    }

                    dfs(depth + 1)

                    if(ladder.areTheConditionsMet()) {
                        if(count < result) result = count
                    }

                    if(connect) {
                        ladder.removeHorizontalLine(horizontalLine)
                        count--
                        connect = false
                    }
                }
            }
        }

        dfs(0)

        return if(result in 1..3) result else -1
    }

    fun getAnswer() : Int = answer
}

fun main() {
    val (n, m, h) = readln().split(Regex("\\s+")).map { it.toShort() }
    val horizontalLines = HashSet<HorizontalLine>()
    for(i in 0 until m) {
        val (a, b) = readln().split(Regex("\\s+")).map { it.toShort() }
        horizontalLines.add(HorizontalLine(a, b)) // b - 1 을 하는 이유는 배열의 인덱스가 0에서 시작하기 때문입니다.
    }

    println("${Solution(n, h, horizontalLines).getAnswer()}")
}
