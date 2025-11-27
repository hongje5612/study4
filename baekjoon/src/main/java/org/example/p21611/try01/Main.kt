package org.example.p21611.try01

/*
    출처 : https://www.acmicpc.net/problem/21611
    제목 : 마법 상어와 블리자드
 */

/**
 * 격자 안의 위치를 나타내는 클래스
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    fun upLocation() : Location = Location(row - 1, col)

    fun downLocation() : Location = Location(row + 1, col)

    fun leftLocation() : Location = Location(row, col - 1)

    fun rightLocation() : Location = Location(row, col + 1)
}

/**
 * 문제에서 제시하는 네가지 방향, 위, 아래, 왼쪽 오른쪽
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun Int.toDirection() : Direction =
    when(this) {
        1 -> Direction.UP
        2 -> Direction.DOWN
        3 -> Direction.LEFT
        4 -> Direction.RIGHT
        else -> throw IllegalArgumentException("방향을 나타내는 숫자는 1~4 이여야 합니다.")
    }

/**
 * 문제에서 말하고 있는 벽의 정보를 담고 있다.
 *
 * @param
 *  up      : true 이면 위쪽에 벽이 있다.
 *  down    : true 이면 아래쪽에 벽이 있다.
 *  left    : true 이면 왼쪽에 벽이 있다.
 *  right   : true 이면 오른쪽에 벽이 있다.
 */
class Wall(var up : Boolean = false, var down : Boolean = false, var left : Boolean = false, var right : Boolean = false)

/**
 * 격자의 한 칸의 정보를 포함한다.
 *
 * @param
 *  number      : cell의 번호
 *  ball        : 구슬의 번호
 *  wall        : 벽의 정보
 */
class Cell(var number : Short, var ball : Byte?, val wall : Wall = Wall())

/**
 * 상어 마법사가 시전하는 블라자드 마법
 *
 * @param
 *  direction   : 방향
 *  distance    : 거리
 */
class Order(val direction : Direction, val distance : Byte)

class Grid(val size : Byte, val board : Array<ByteArray>, val orders : List<Order>) {
    /*
        격자 내에 포함된 임의의 칸을 정보를 담고 있는 변수
     */
    private val cells = Array(size.toInt()) { row ->
        Array<Cell>(size.toInt()) { col ->
            val t = if(board[row][col] != 0.toByte()) board[row][col] else null
            Cell(0, t)
        }
    }
    //격자의 중앙을 표시하는 인덱스
    private val centerIndex = size / 2

    private fun isValidLocation(row : Byte, col : Byte) : Boolean = (row in 0 until size) && (col in 0 until size)

    private fun isValidLocation(location : Location) : Boolean = isValidLocation(location.row, location.col)

    private inner class TornadoMaker {
        /**
         * 토네이도를 만들 때 현제 위치와 방향을 나타내는 클래스
         *
         * @param
         *  location : 위치
         *  direction : 방향
         */
        inner class Tornado(val location : Location, val direction: Direction)

        // 소용돌이 모양을 따라가면서 방문한 경우 true로 설정한다.
        private val visited = Array(size.toInt()) { BooleanArray(size.toInt()) { false } }

        init {
            var row = centerIndex
            var col = centerIndex

            // 상어의 위치
            cells[row][col].wall.up = true
            cells[row][col].wall.down = true
            cells[row][col].wall.right = true
            cells[row][col].number = 0
            cells[row][col].ball = null
            visited[row][col] = true   // 방문했음

            make()
        }

        /**
         * 토네이도의 회전 방향으로 돌면서, 부모 클래스의 멤버 변수 cells 의 wall 정보를 설정합니다.
         */
        fun make() {
            var currentTornado = Tornado(Location(centerIndex, centerIndex).leftLocation(), Direction.LEFT)
            var number : Short = 1

            while(isValidLocation(currentTornado.location)) {
                cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].number = number++
                visited[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()] = true

                when(currentTornado.direction) {
                    Direction.UP -> {
                        val leftLocation = currentTornado.location.leftLocation()
                        if(!isValidLocation(leftLocation)) return

                        if(visited[leftLocation.row.toInt()][leftLocation.col.toInt()]) {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.left = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.right = true
                            currentTornado = Tornado(currentTornado.location.upLocation(), Direction.UP)
                        } else {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.up = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.right = true
                            currentTornado = Tornado(leftLocation, Direction.LEFT)
                        }
                    }

                    Direction.DOWN -> {
                        val rightLocation = currentTornado.location.rightLocation()
                        if(!isValidLocation(rightLocation)) return

                        if(visited[rightLocation.row.toInt()][rightLocation.col.toInt()]) {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.left = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.right = true
                            currentTornado = Tornado(currentTornado.location.downLocation(), Direction.DOWN)
                        } else {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.down = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.left = true
                            currentTornado = Tornado(rightLocation, Direction.RIGHT)
                        }
                    }

                    Direction.LEFT -> {
                        val downLocation = currentTornado.location.downLocation()
                        if(!isValidLocation(downLocation)) return

                        if(visited[downLocation.row.toInt()][downLocation.col.toInt()]) {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.up = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.down = true
                            currentTornado = Tornado(currentTornado.location.leftLocation(), Direction.LEFT)
                        } else {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.up = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.left = true
                            currentTornado = Tornado(downLocation, Direction.DOWN)
                        }
                    }

                    Direction.RIGHT -> {
                        val upLocation = currentTornado.location.upLocation()
                        if(!isValidLocation(upLocation)) return

                        if(visited[upLocation.row.toInt()][upLocation.col.toInt()]) {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.up = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.down = true
                            currentTornado = Tornado(currentTornado.location.rightLocation(), Direction.RIGHT)
                        } else {
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.down = true
                            cells[currentTornado.location.row.toInt()][currentTornado.location.col.toInt()].wall.right = true
                            currentTornado = Tornado(upLocation, Direction.UP)
                        }
                    }
                }
            }
        }
    }
    
    init {
        TornadoMaker()      // 격자에 토네이도 모양을 구성 정보를 cells 변수에 넣는다
    }

    /**
     * 명령에 따라 구슬이 깨지다.
     *
     * @param
     *  order : 명령
     */
    private fun theBeadsBreak(order : Order) {
        var location = Location(centerIndex, centerIndex)     // 상어의 위치

        when(order.direction) {
            Direction.UP -> {
                for(i in 0 until order.distance) {
                    val nextLocation = location.upLocation()
                    cells[nextLocation.row.toInt()][nextLocation.col.toInt()].ball = null
                    location = nextLocation
                }
            }

            Direction.DOWN -> {
                for(i in 0 until order.distance) {
                    val nextLocation = location.downLocation()
                    cells[nextLocation.row.toInt()][nextLocation.col.toInt()].ball = null
                    location = nextLocation
                }
            }

            Direction.LEFT -> {
                for(i in 0 until order.distance) {
                    val nextLocation = location.leftLocation()
                    cells[nextLocation.row.toInt()][nextLocation.col.toInt()].ball = null
                    location = nextLocation
                }
            }

            Direction.RIGHT -> {
                for(i in 0 until order.distance) {
                    val nextLocation = location.rightLocation()
                    cells[nextLocation.row.toInt()][nextLocation.col.toInt()].ball = null
                    location = nextLocation
                }
            }
        }
    }

    /**
     * @return
     *  현제 위치에서 토네이도를 따라서 다음 위치를 반환한다.
     *  다음 위치가 존재하지 않으면 null을 반환한다
     */
    private fun Location.nextLocation() : Location? {
        val cell = cells[row.toInt()][col.toInt()]
        var r : Byte = row
        var c : Byte = col

        if(!cell.wall.up) {
            val upLocation = this.upLocation()
            val upCell = cells[upLocation.row.toInt()][upLocation.col.toInt()]
            if(upCell.number > cell.number) r = upLocation.row
        }

        if(!cell.wall.down) {
            val downLocation = this.downLocation()
            val downCell = cells[downLocation.row.toInt()][downLocation.col.toInt()]
            if(downCell.number > cell.number)  r = downLocation.row
        }

        if(!cell.wall.left) {
            val leftLocation = this.leftLocation()
            if(!isValidLocation(leftLocation)) return null
            val leftCell = cells[leftLocation.row.toInt()][leftLocation.col.toInt()]
            if(leftCell.number > cell.number) c = leftLocation.col
        }

        if(!cell.wall.right) {
            val rightLocation = this.rightLocation()
            val rightCell = cells[rightLocation.row.toInt()][rightLocation.col.toInt()]
            if(rightCell.number > cell.number) c = rightLocation.col
        }

        return Location(r, c)
    }

    /**
     * 빈 공간을 메우기 위해서 구슬이 이동한다.
     */
    private fun theBeadsMove() {
        var currentLocation = Location(centerIndex, centerIndex)
        var emptyLocation : Location? = null

        while(true) {
            currentLocation = currentLocation.nextLocation() ?: return
            val cell = cells[currentLocation.row.toInt()][currentLocation.col.toInt()]
            if(cell.ball == null) {
                if(emptyLocation == null) emptyLocation = currentLocation
            } else {
                if(emptyLocation != null) {
                    val emptyCell = cells[emptyLocation.row.toInt()][emptyLocation.col.toInt()]
                    emptyCell.ball = cell.ball
                    cell.ball = null
                    emptyLocation = emptyLocation.nextLocation()
                }
            }
        }
    }

    /**
     * 폭발하는 구슬은 4개 이상 연속하는 구슬이 있을 때 발생한다
     */
    private fun theBeadsExplode() : Int {
        val LIMIT = 4   // 4개 이상의 구슬이 폭발한다.
        var score = 0   // 폭발하는 볼의 점수를 합산한 값

        /**
         * bfs 알고리즘
         * 현제 위치에서 시작하여 동일한 볼의 개수를 찾고, 찾은 볼 들을 지운 후 점수를 반환한다.
         */
        fun bfs(location : Location) : Int {
            val queue = ArrayDeque<Location>()
            val set = HashSet<Location>()
            val ball = cells[location.row.toInt()][location.col.toInt()].ball
            queue.addLast(location)

            while(queue.isNotEmpty()) {
                val loc = queue.removeFirst()
                set.add(loc)

                val nextLocation = loc.nextLocation() ?: continue
                val cell = cells[nextLocation.row.toInt()][nextLocation.col.toInt()]
                if(cell.ball == ball) queue.addLast(nextLocation)
            }

            if(set.size >= LIMIT) {
                val score = ball!! * set.size
                for(l in set) {
                    cells[l.row.toInt()][l.col.toInt()].ball = null
                }
                return score
            } else return 0
        }

        var currentLocation : Location? = Location(centerIndex, centerIndex).leftLocation()
        while(currentLocation != null) {
            val cell = cells[currentLocation.row.toInt()][currentLocation.col.toInt()]
            if(cell.ball != null) score += bfs(currentLocation)
            currentLocation = currentLocation.nextLocation()
        }

        return score
    }

    /**
     * 구슬이 변화하는 단계
     * 하나의 그룹은 두 개의 구슬 A와 B로 변한다.
     * 구슬 A의 번호는 그룹에 들어있는 구슬의 개수이고,
     * B는 그룹을 이루고 있는 구슬의 번호이다.
     * 구슬은 다시 그룹의 순서대로 1번 칸부터 차례대로 A, B의 순서로 칸에 들어간다.
     * 다음 그림은 구슬이 변화한 후이고, 색은 구분하기 위해 위의 그림에 있는 그룹의 색을 그대로 사용했다.
     * 만약, 구슬이 칸의 수보다 많아 칸에 들어가지 못하는 경우 그러한 구슬은 사라진다.
     */
    private fun theBeadsChange() {
        val balls = ArrayList<Byte>()
        var currentLocation = Location(centerIndex, centerIndex).leftLocation()

        while(true) {
            val ball = cells[currentLocation.row.toInt()][currentLocation.col.toInt()].ball ?: break
            balls.add(ball)
            currentLocation = currentLocation.nextLocation() ?: break
        }

        val newBalls = ArrayList<Byte>()
        var count : Byte = 0
        var ball : Byte? = null

        for(t in balls) {
            if(ball == null) {
                ball = t
                count = 1
            } else {
                if(t == ball) {
                    count++
                } else {
                    newBalls.add(count)
                    newBalls.add(ball)
                    ball = t
                    count = 1
                }
            }
        }

        if(ball != null && newBalls.last() != ball) {
            newBalls.add(count)
            newBalls.add(ball)
        }

        currentLocation = Location(centerIndex, centerIndex)
        val it = newBalls.iterator()
        while(true) {
            currentLocation = currentLocation.nextLocation() ?: break
            val cell = cells[currentLocation.row.toInt()][currentLocation.col.toInt()]
            if(it.hasNext()) cell.ball = it.next() else cell.ball = null
        }
    }

    /**
     * 브리자드 마법을 시전합니다.
     */
    private fun castBlizzardMagic(order : Order) : Int {
        var totalScore = 0


        theBeadsBreak(order)

        while(true) {
            theBeadsMove()

            val score = theBeadsExplode()

            if(score == 0) break
            totalScore += score
        }

        theBeadsChange()

        return totalScore
    }

    fun simulate() : Int {
        var totalScore = 0

        for(order in orders) {
            totalScore += castBlizzardMagic(order)
        }
        return totalScore
    }

    fun show() {
        var currentLocation = Location(centerIndex, centerIndex)

        while(true) {
            val cell = cells[currentLocation.row.toInt()][currentLocation.col.toInt()]
            if(cell.ball != null) print("${cell.ball} ") else print("0 ")
            currentLocation = currentLocation.nextLocation() ?: break
        }
        println()
    }
}

fun main() {
    val (n, m) = readln().split(Regex("\\s+")).map { it.toByte() }
    val board = Array(n.toInt()) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }
    val orders = ArrayList<Order>()
    for(i in 1..m) {
        val (direction, distance) = readln().split(Regex("\\s+")).map { it.toByte() }
        orders.add(Order(direction.toInt().toDirection(), distance))
    }

    println("${Grid(n, board, orders).simulate()}")
}
