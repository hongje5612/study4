package org.example.p5373

/**
 * 출처 : https://www.acmicpc.net/problem/5373
 *
 * @author
 *  조홍제 : https://blog.naver.com/hjj5612
 *
 * 문제를 풀려면 큐브를 하나 구매하는 것이 좋습니다. 저도 구매를 했습니다.
 * 구매를 했다면 각 면에 배열의 인덱스를 적어 놓고, 그 순수데로 코딩을 하시면 됩니다.
 * 저는 배열의 인덱스를 큐브에 다 적어 놓았는데도 헷갈려서, 여러번 수정을 했습니다.
 *
 * 큐븡에 인덱스를 적는 방법은 윗 면 만 문제에서 제시 하는 방향으로 정확하게 적어 주시고,
 * 나머지는 문제를 푸는 사람 마음대로 하시면 됩니다.
 * 그러면 제가 코딩한 것과는 조금 다른 순서로 인덱스가 코딩되지만 결과를 같습니다.
 */

/*
    사용되는 색상의 정의
 */
const val WHITE = 'w'       // 윗 면의 색상
const val YELLOW = 'y'      // 아랫 면의 색상
const val RED = 'r'         // 앞 면의 색상
const val ORANGE = 'o'      // 뒷 면의 색상
const val GREEN = 'g'       // 왼쪽 면의 색상
const val BLUE = 'b'        // 오른쪽 면의 색상

/**
 * 색상을 정의하는 클래스
 */
class Color(var value : Char)

/**
 * 3 x 3 큐브를 정의 합니다.
 */
class Cube {
    companion object {
        const val SIZE = 3        // 큐브의 크기 size x size x size
    }

    private val topSurface = Array(SIZE) { Array(SIZE) { Color(WHITE) } }
    private val bottomSurface = Array(SIZE) { Array(SIZE) { Color(YELLOW) } }
    private val frontSurface = Array(SIZE) { Array(SIZE) { Color(RED) } }
    private val backSurface = Array(SIZE) { Array(SIZE) { Color(ORANGE) } }
    private val leftSurface = Array(SIZE) { Array(SIZE) { Color(GREEN) } }
    private val rightSurface = Array(SIZE) { Array(SIZE) { Color(BLUE) } }

    /**
     * 배열을 오른쪽으로 회전한다.
     * 마지막에 있는 값이 처음으로 오고, 모던 값들을 오른쪽으로 한 칸 이동 한다.
     *
     * @param
     *  cnt : 오른쪽으로 회전하는 작업을 몇 번 반복할지 결정한다.
     */
    private fun Array<Color>.rotateRight(cnt : Int) {
        val queue = ArrayDeque<Char>()
        for(i in 0 until cnt) queue.addLast(this[lastIndex - i].value)
        var j = lastIndex
        for(i in (this.lastIndex - cnt) downTo 0) this[j--].value = this[i].value
        j = cnt
        while(queue.isNotEmpty()) this[--j].value = queue.removeFirst()
    }

    /**
     * 배열을 왼쪽으로 회전한다.
     * 처음 값은 마지막에 가고, 모던 값들은 왼쪽으로 한 칸 이동한다.
     *
     * @param
     *  cnt : 왼쪽으로 회전하는 작업을 몇 번 반복할지 결정한다.
     */
    private fun Array<Color>.rotateLeft(cnt : Int) {
        val queue = ArrayDeque<Char>()
        for(i in 0 until cnt) queue.addLast(this[i].value)
        var j = 0
        for(i in cnt until size) this[j++].value = this[i].value
        j = size - cnt
        while(queue.isNotEmpty()) this[j++].value = queue.removeFirst()
    }

    /**
     * 배열 3x3 를 시계방향으로 90도 회전합니다.
     */
    private fun Array<Array<Color>>.rotateClockwise90() {
        val arr = arrayOf(this[0][0], this[0][1], this[0][2], this[1][2], this[2][2], this[2][1], this[2][0], this[1][0])
        arr.rotateRight(SIZE - 1)
    }


    /**
     * 배열 3x3 를(을) 시계방향으로 90도 회전합니다.
     */
    private fun Array<Array<Color>>.rotateCounterClockwise90() {
        val arr = arrayOf(this[0][0], this[0][1], this[0][2], this[1][2], this[2][2], this[2][1], this[2][0], this[1][0])
        arr.rotateLeft(SIZE - 1)
    }

    /*
        윗 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheTopSurface = arrayOf(
        backSurface[2][0], backSurface[2][1], backSurface[2][2],
        rightSurface[0][0], rightSurface[1][0], rightSurface[2][0],
        frontSurface[0][2], frontSurface[0][1], frontSurface[0][0],
        leftSurface[2][2], leftSurface[1][2], leftSurface[0][2]
    )

    /*
        아랫 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheBottomSurface = arrayOf(
        frontSurface[2][0], frontSurface[2][1], frontSurface[2][2],
        rightSurface[2][2], rightSurface[1][2], rightSurface[0][2],
        backSurface[0][2], backSurface[0][1], backSurface[0][0],
        leftSurface[0][0], leftSurface[1][0], leftSurface[2][0]
    )

    /*
        앞 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheFrontSurface = arrayOf(
        topSurface[2][0], topSurface[2][1], topSurface[2][2],
        rightSurface[2][0], rightSurface[2][1], rightSurface[2][2],
        bottomSurface[0][2], bottomSurface[0][1], bottomSurface[0][0],
        leftSurface[2][0], leftSurface[2][1], leftSurface[2][2]
    )

    /*
        뒷 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheBackSurface = arrayOf(
        bottomSurface[2][0], bottomSurface[2][1], bottomSurface[2][2],
        rightSurface[0][2], rightSurface[0][1], rightSurface[0][0],
        topSurface[0][2], topSurface[0][1], topSurface[0][0],
        leftSurface[0][2], leftSurface[0][1], leftSurface[0][0]
    )

    /*
        왼쪽 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheLeftSurface = arrayOf(
        backSurface[0][0], backSurface[1][0], backSurface[2][0],
        topSurface[0][0], topSurface[1][0], topSurface[2][0],
        frontSurface[0][0], frontSurface[1][0], frontSurface[2][0],
        bottomSurface[0][0], bottomSurface[1][0], bottomSurface[2][0]
    )

    /*
        오른쪽 면 주위에 있는 색상 정보, 시계방향으로 적혀 있다.
     */
    private val colorsAssociatedWithTheRightSurface = arrayOf(
        backSurface[2][2], backSurface[1][2], backSurface[0][2],
        bottomSurface[2][2], bottomSurface[1][2], bottomSurface[0][2],
        frontSurface[2][2], frontSurface[1][2], frontSurface[0][2],
        topSurface[2][2], topSurface[1][2], topSurface[0][2]
    )

    /**
     * 윗 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheTopSurfaceClockwise90() {
        topSurface.rotateClockwise90()
        colorsAssociatedWithTheTopSurface.rotateRight(SIZE)
    }

    /**
     * 아랫 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheBottomSurfaceClockwise90() {
        bottomSurface.rotateClockwise90()
        colorsAssociatedWithTheBottomSurface.rotateRight(SIZE)
    }

    /**
     * 앞 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheFrontSurfaceClockwise90() {
        frontSurface.rotateClockwise90()
        colorsAssociatedWithTheFrontSurface.rotateRight(SIZE)
    }

    /**
     * 뒷 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheBackSurfaceClockwise90() {
        backSurface.rotateClockwise90()
        colorsAssociatedWithTheBackSurface.rotateRight(SIZE)
    }

    /**
     * 왼쪽 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheLeftSurfaceClockwise90() {
        leftSurface.rotateClockwise90()
        colorsAssociatedWithTheLeftSurface.rotateRight(SIZE)
    }

    /**
     * 오른쪽 면을 시계 방향으로 90도 회전합니다.
     */
    fun rotateTheRightSurfaceClockwise90() {
        rightSurface.rotateClockwise90()
        colorsAssociatedWithTheRightSurface.rotateRight(SIZE)
    }

    /**
     * 윗 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheTopSurfaceCounterClockwise90() {
        topSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheTopSurface.rotateLeft(SIZE)
    }

    /**
     * 아랫 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheBottomSurfaceCounterClockwise90() {
        bottomSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheBottomSurface.rotateLeft(SIZE)
    }

    /**
     * 앞 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheFrontSurfaceCounterClockwise90() {
        frontSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheFrontSurface.rotateLeft(SIZE)
    }

    /**
     * 뒷 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheBackSurfaceCounterClockwise90() {
        backSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheBackSurface.rotateLeft(SIZE)
    }

    /**
     * 왼쪽 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheLeftSurfaceCounterClockwise90() {
        leftSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheLeftSurface.rotateLeft(SIZE)
    }

    /**
     * 오른쪽 면을 반시계 방향으로 90도 회전합니다.
     */
    fun rotateTheRightSurfaceCounterClockwise90() {
        rightSurface.rotateCounterClockwise90()
        colorsAssociatedWithTheRightSurface.rotateLeft(SIZE)
    }

    /**
     * 윗면의 색상 정보를 출력 합니다.
     */
    fun show() {
        for(colors in topSurface) {
            for(color in colors) {
                print(color.value)
            }
            println()
        }
    }
}

fun simulate(command : String) {
    val tokens = command.split(Regex("\\s+"))
    val cube = Cube()

    for(token in tokens) {
        val surface = token[0]
        val direction = token[1]

        when(surface) {
            'U' -> {
                when(direction) {
                    '+' -> cube.rotateTheTopSurfaceClockwise90()
                    '-' -> cube.rotateTheTopSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            'D' -> {
                when(direction) {
                    '+' -> cube.rotateTheBottomSurfaceClockwise90()
                    '-' -> cube.rotateTheBottomSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            'F' -> {
                when(direction) {
                    '+' -> cube.rotateTheFrontSurfaceClockwise90()
                    '-' -> cube.rotateTheFrontSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            'B' -> {
                when(direction) {
                    '+' -> cube.rotateTheBackSurfaceClockwise90()
                    '-' -> cube.rotateTheBackSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            'L' -> {
                when(direction) {
                    '+' -> cube.rotateTheLeftSurfaceClockwise90()
                    '-' -> cube.rotateTheLeftSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            'R' -> {
                when(direction) {
                    '+' -> cube.rotateTheRightSurfaceClockwise90()
                    '-' -> cube.rotateTheRightSurfaceCounterClockwise90()
                    else -> throw IllegalArgumentException()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    cube.show()
}

fun main() {
    val testCase = readln().toInt()         // 테스트 케이스 수
    val commands = ArrayList<String>()
    for(i in 0 until testCase) {
        readln()        // 큐브를 돌린 횟수, 입력으로 주어지기 때문에 읽을 뿐이다. 코드 상에서는 전혀 필요 없다.
        commands.add(readln())
    }

    for(command in commands) {
        simulate(command)
    }
}
