package org.example.p23290

import java.util.Objects

/**
 * 문제 출처 : https://www.acmicpc.net/problem/23290
 *
 * @author
 *  조홍제 (https://blog.naver.com/hjj5612)
 */

const val SIZE = 4      // 격자판의 크기가 4 X 4 이다

/**
 * 격자판 내의 위치를 지정한다.
 *
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Byte) : this(row.toByte(), col)

    // 복사 생성자
    constructor(location: Location) : this(location.row, location.col)

    fun north() : Location = Location(row - 1, col)             // 현제 위치의 북쪽
    fun northEast() : Location = Location(row - 1, col + 1)     // 현제 위치의 북동쪽
    fun east() : Location = Location(row, col + 1)              // 현제 위치의 동쪽
    fun southEast() : Location = Location(row + 1, col + 1)     // 현제 위치의 남동쪽
    fun south() : Location = Location(row + 1 , col)            // 현제 위치의 남쪽
    fun southWest() : Location = Location(row + 1, col - 1)     // 현제 위치의 남서쪽
    fun west() : Location = Location(row , col - 1)             // 현제 위치의 서쪽
    fun northWest() : Location = Location(row - 1, col - 1)     // 현제 위치의 북서쪽

    fun isValid() : Boolean = (row in 0 until SIZE) && (col in 0 until SIZE)    // 격자판을 벗어나지 않은 위치이면 참을 반환한다.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (row != other.row) return false
        if (col != other.col) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(row, col)
    }

    override fun toString(): String {
        return "Location(row=$row, col=$col)"
    }
}

/**
 * 8가지 방향을 나타내는 열거형 자료형
 */
enum class Direction {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

    /**
     * @return
     *  현제 방향에서 반시계 방향으로 45도 회전한 방향을 반환한다.
     */
    fun rotateCounterClockwise45() : Direction {
        val directions = arrayOf(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST)
        var index = this.ordinal - 1
        if(index < 0) index = directions.lastIndex
        return directions[index]
    }
}

/**
 * 상어 클래스
 *
 * @param
 *  currentLocation     : 상어의 현제 위치
 */
class Shark(var currentLocation : Location)

/**
 * 물고기 클래스
 *
 * @param
 *  direction       : 물고기의 방향
 */
class Fish(var direction : Direction) {
    companion object {
        val ss = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    }
    // 복사 생성자
    constructor(fish : Fish) : this(fish.direction)

    override fun toString(): String {
        val index = direction.ordinal
        return ss[index]
    }
}

/**
 * 냄새를 나타내는 클래스
 *
 * @param
 *  round   : 몇 번째 연습에서 나타난 냄새인지를 가르키는 변수
 */
class Smell(val round : Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Smell

        return round == other.round
    }

    override fun hashCode(): Int {
        return round
    }
}

class Cell(val fish : MutableList<Fish>, val smell : MutableSet<Smell>)

// 4 x 4 격자
val grid : Array<Array<Cell>> = Array<Array<Cell>>(SIZE) { Array<Cell>(SIZE) { Cell(ArrayList<Fish>(), HashSet<Smell>()) } }

// 복제 마법시 시전되면 이 곳에 물고기들을 복사 해 놓는다.
val temporaryCopyGrid : Array<Array<MutableList<Fish>>> = Array(SIZE) { Array(SIZE) { ArrayList<Fish>() } }

// 4 x 4 격자, 물고기가 이동할 때 사용된다.
val temporaryGrid : Array<Array<MutableList<Fish>>> = Array(SIZE) { Array(SIZE) { ArrayList<Fish>() } }

// 현제 위치에서 다음 위치를 반환하는 함수들, 물고기의 현제 위치에서 다음 위치를 반환하는 함수들
val nextLocationFunctionOfFish = arrayOf(
    Location::north, Location::northEast, Location::east, Location::southEast,
    Location::south, Location::southWest, Location::west, Location::northWest,
)

// 현제 위치에서 다음 위치를 반환하는 함수들, 상어의 현제 위치에서 다음 위치를 반환하는 함수들
val nextLocationFunctionOfShark = arrayOf(Location::north, Location::west, Location::south, Location::east)

// 상어, 초기화 작업을 하지만 의미는 없다. 사용되지 않기 때문이다.
val shark : Shark = Shark(Location(0, 0))

/**
 * 복제 마법을 시전하다.
 */
fun castCopySpell() {
    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            val allTheFish = grid[row][col].fish
            for(fish in allTheFish) {
                temporaryCopyGrid[row][col].add(Fish(fish))
            }
        }
    }
}

/**
 * 물고기들을 움직인다.
 */
fun moveAllTheFish() {
    val buffer : Array<Array<MutableList<Fish>>> = temporaryGrid        // 물고기들이 잠시 저장되는 공간

    /**
     * 물고기의 방향에서 따라, 이동하여. buffer에 물고기를 복사한다.
     *
     * @param
     *  fish : 물고기
     *  currentLocation : 물고기의 현제 방향
     */
    fun copyToBuffer(fish : Fish, currentLocation : Location) {
        var isCopied = false        // 복사 되지 않았음.

        for(i in 0 until nextLocationFunctionOfFish.size) {
            val index = fish.direction.ordinal
            val nextLocation = nextLocationFunctionOfFish[index].invoke(currentLocation)
            if(nextLocation.isValid() && shark.currentLocation != nextLocation) {
                val smell = grid[nextLocation.row.toInt()][nextLocation.col.toInt()].smell
                if(smell.isEmpty()) {
                    buffer[nextLocation.row.toInt()][nextLocation.col.toInt()].add(fish)
                    isCopied = true     // 복사 되었음
                    break
                }
            }
            fish.direction = fish.direction.rotateCounterClockwise45()
        }

        if(!isCopied) {
            buffer[currentLocation.row.toInt()][currentLocation.col.toInt()].add(fish)
        }
    }

    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            for(fish in grid[row][col].fish) {
                copyToBuffer(fish, Location(row, col))
            }
        }
    }

    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            grid[row][col].fish.clear()
            grid[row][col].fish.addAll(buffer[row][col])
            buffer[row][col].clear()
        }
    }
}

/**
 * 3. 상어가 연속해서 3칸 이동한다.
 *    상어는 현재 칸에서 상하좌우로 인접한 칸으로 이동할 수 있다.
 *    연속해서 이동하는 칸 중에 격자의 범위를 벗어나는 칸이 있으면,
 *    그 방법은 불가능한 이동 방법이다. 연속해서 이동하는 중에 상어가 물고기가 있는 같은 칸으로 이동하게 된다면,
 *    그 칸에 있는 모든 물고기는 격자에서 제외되며, 제외되는 모든 물고기는 물고기 냄새를 남긴다.
 *    가능한 이동 방법 중에서 제외되는 물고기의 수가 가장 많은 방법으로 이동하며,
 *    그러한 방법이 여러가지인 경우 사전 순으로 가장 앞서는 방법을 이용한다. 사전 순에 대한 문제의 하단 노트에 있다.
 *
 *    노트
 *    상어의 이동 방법 중 사전 순으로 가장 앞서는 방법을 찾으려면 먼저, 방향을 정수로 변환해야 한다.
 *    상은 1, 좌는 2, 하는 3, 우는 4로 변환한다. 변환을 모두 마쳤으면, 수를 이어 붙여 정수로 하나 만든다.
 *    두 방법 A와 B가 있고, 각각을 정수로 변환한 값을 a와 b라고 하자. a < b를 만족하면 A가 B보다 사전 순으로 앞선 것이다.
 *
 *    예를 들어, [상, 하, 좌]를 정수로 변환하면 132가 되고, [하, 우, 하]를 변환하면 343이 된다.
 *    132 < 343이기 때문에, [상, 하, 좌]가 [하, 우, 하]보다 사전 순으로 앞선다.
 *
 *    총 43 = 64가지 방법을 사전 순으로 나열해보면 [상, 상, 상], [상, 상, 좌], [상, 상, 하], [상, 상, 우], [상, 좌, 상],
 *    [상, 좌, 좌], [상, 좌, 하], [상, 좌, 우], [상, 하, 상], ...,
 *    [우, 하, 하], [우, 하, 우], [우, 우, 상], [우, 우, 좌], [우, 우, 하], [우, 우, 우] 이다.
 *
 * @param
 *  round : 몇 번째 연습(상어가 하는 마법)인지를 나타내는 변수
 */
fun moveShark(round : Int) {

    /**
     * @param
     *  firstMovement   : 1~4, 상은 1, 좌는 2, 하는 3, 우는 4
     *  secondMovement  : 1~4
     *  thirdMovement   : 1~4
     *
     * @return
     *  상어가 firstMovement, secondMovement, thirdMovement 순으로 움직일 수 있으면, 경로를 따라 지나온 자리의 물고기들의 합을 반환한다.
     *  그렇지 못 하면 -1를 반환한다.
     */
    fun canASharkMoveLikeThis(firstMovement : Int, secondMovement : Int, thirdMovement : Int) : Int {
        val movements = arrayOf(firstMovement, secondMovement, thirdMovement)
        var currentLocation = shark.currentLocation
        val visited = HashSet<Location>()      // 방문한 위치는 여기에 저장한다.
        var retValue = 0

        for(movement in movements) {
            val nextLocation = nextLocationFunctionOfShark[movement - 1].invoke(currentLocation)
            if(nextLocation.isValid()) {
                if(!visited.contains(nextLocation)) {       // 방문한 적이 없다면
                    retValue += grid[nextLocation.row.toInt()][nextLocation.col.toInt()].fish.size      // 현제 물고기 량을 누적한다.
                    visited.add(nextLocation)
                }
            } else return -1
            currentLocation = nextLocation
        }
        return retValue
    }

    /**
     * @return
     *  가장 많은 물고기를 먹을 수 있는 최상의 방법을 찾아서, 그 길로 가는 방법을 반환한다.
     *  반환하는 값응 firstMovement * 100 + secondMovement * 10 + thirdMovement 이다.
     */
    fun findTheBestWayToEatMostFish() : Int {
        var max = 0     // 상어가 물고기를 잡아 먹는 양 중에서 최대값, 상어가 지나가는 경로에 따라서 다르다.
        var paths = ArrayList<Int>()    // 상어가 지나가는 경로들, firstMovement * 100 + secondMovement * 10 + 1 형식으로 저장된다.

        for(firstMovement in 1 ..4) {
            for(secondMovement in 1..4) {
                for(thirdMovement in 1..4) {
                    val amount = canASharkMoveLikeThis(firstMovement, secondMovement, thirdMovement)
                    if(amount == -1) continue

                    if(amount > max) {
                        max = amount
                        paths = ArrayList<Int>()
                        paths.add(firstMovement * 100 + secondMovement * 10 + thirdMovement)
                    } else if(amount == max) {
                        paths.add(firstMovement * 100 + secondMovement * 10 + thirdMovement)
                    }
                }
            }
        }

        if(paths.isEmpty()) throw IllegalStateException("No paths found. Shark cannot move")
        else if(paths.size == 1) return paths[0]
        else {
            paths.sort()
            return paths[0]
        }
    }

    val path = findTheBestWayToEatMostFish()
    val firstMovement = path / 100
    val rem = path % 100
    val secondMovement = rem / 10
    val thirdMovement = rem % 10
    val movements = arrayOf(firstMovement, secondMovement, thirdMovement)
    var currentLocation = shark.currentLocation
    var cell = grid[currentLocation.row.toInt()][currentLocation.col.toInt()]

    for(movement in movements) {
        val nextLocation = nextLocationFunctionOfShark[movement - 1].invoke(currentLocation)
        cell = grid[nextLocation.row.toInt()][nextLocation.col.toInt()]
        if(cell.fish.isNotEmpty()) cell.smell.add(Smell(round))
        cell.fish.clear()   // 상어가 다 잡아 먹는다.
        currentLocation = nextLocation
    }
    shark.currentLocation = currentLocation     // 상어의 최종 위치이다.
}

/**
 * 두 번 전 연습에서 생긴 물고기의 냄새가 격자에서 사라진다.
 *
 * @param
 *  currentRound    : 현제 몇 번째 연습인지를 나타내는 변수
 */
fun removeSmell(currentRound : Int) {
    val round = currentRound - 2

    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            val cell = grid[row][col]
            cell.smell.remove(Smell(round))
        }
    }
}

/**
 * 복제 마법이 완료된다.
 */
fun completeCopySpell() {
    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            val list = temporaryCopyGrid[row][col]
            grid[row][col].fish.addAll(list)
            list.clear()
        }
    }
}

/**
 * @return
 *  격자판에 몇 마리의 물고기가 남았는지를 반환한다.
 */
fun howManyFishDoWeHaveLeft() : Int {
    var answer = 0
    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            val cell = grid[row][col]
            answer += cell.fish.size
        }
    }
    return answer
}

/**
 * 문제에서 제시한 5가지 과정을 모의 테스트 한다.
 *
 * @param
 *  s : 마법을 연습한 횟 수
 *
 * @return
 *  격자판에 몇 마리의 물고기가 남았는지를 반환한다.
 */
fun simulate(s : Byte) : Int {
    for(round in 1..s) {
        castCopySpell()                     // 1. 복사 마법은 시전하다.

        moveAllTheFish()                    // 2. 모든 물고기가 움직인다.

        moveShark(round)                    // 3. 상어가 움직인다.

        removeSmell(round)     // 4. 2회전에 발생한 물고기 냄새를 제거하다.

        completeCopySpell()                 // 5. 복사 마법이 완료된다.
    }
    return howManyFishDoWeHaveLeft()
}

/*
fun show() {
    for(row in 0 until SIZE) {
        for(col in 0 until SIZE) {
            val cell = grid[row][col]
            print("(")
            cell.fish.forEach { print("${it},")}
            if(cell.smell.isNotEmpty()) print("*")
            print(") ")
        }
        println()
    }
}
*/

fun main() {
    val (m, s) = readln().split(Regex("\\s+")).map { it.toByte() } // m : 물고기의 수, s : 상어가 마법을 연습한 횟 수

    //물고기의 위치와 방향을 입력 받는다.
    val dir = arrayOf(
        Direction.WEST, Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST,
        Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST,
    )
    for(i in 0 until m) {
        val (fx, fy, d) = readln().split(Regex("\\s+")).map { it.toByte() }     // (fx, fy) : 물고기의 위치, d : 물고기의 방향
        val row = fx - 1    // 배열의 인덱스가 0이므로 1를 빼준다.
        val col = fy - 1
        val cell = grid[row][col]
        cell.fish.add(Fish(dir[d - 1]))     // 배열의 인덱스가 0 이므로 1를 빼준다.
    }

    // 상어의 위치를 입력 받는다.
    val (sx, sy) = readln().split(Regex("\\s+")).map { it.toByte() }
    shark.currentLocation = Location(sx - 1, sy - 1)    // 배열의 인덱스가 0 이므로 1를 빼준다.

    println(simulate(s))
}
