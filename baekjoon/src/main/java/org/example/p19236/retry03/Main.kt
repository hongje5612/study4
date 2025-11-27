package org.example.p19236.retry03

const val SIZE : Int = 4

class Location(val row : Byte, val col : Byte) {
    constructor(other : Location) : this(other.row, other.col)
    constructor(row : Byte, col : Int) : this(row, col.toByte())
    constructor(row : Int, col : Byte) : this(row.toByte(), col)
    constructor(row : Int, col : Int) : this(row.toByte(), col.toByte())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (row != other.row) return false
        if (col != other.col) return false

        return true
    }

    override fun hashCode(): Int {
        var result : Int = row.toInt()
        result = 31 * result + col
        return result
    }
}

fun Location.oneStep(direction : Direction) =
    when(direction) {
        Direction.NORTH -> Location(this.row - 1, this.col)
        Direction.NORTH_WEST -> Location(this.row - 1, this.col - 1)
        Direction.WEST -> Location(this.row, this.col - 1)
        Direction.SOUTH_WEST -> Location(this.row + 1, this.col - 1)
        Direction.SOUTH -> Location(this.row + 1, this.col)
        Direction.SOUTH_EAST -> Location(this.row + 1, this.col + 1)
        Direction.EAST -> Location(this.row, this.col + 1)
        Direction.NORTH_EAST -> Location(this.row - 1, this.col + 1)
    }

enum class Direction {
    NORTH,
    NORTH_WEST,
    WEST,
    SOUTH_WEST,
    SOUTH,
    SOUTH_EAST,
    EAST,
    NORTH_EAST
}

fun Direction.rotate45() =
    when(this) {
        Direction.NORTH -> Direction.NORTH_WEST
        Direction.NORTH_WEST -> Direction.WEST
        Direction.WEST -> Direction.SOUTH_WEST
        Direction.SOUTH_WEST -> Direction.SOUTH
        Direction.SOUTH -> Direction.SOUTH_EAST
        Direction.SOUTH_EAST -> Direction.EAST
        Direction.EAST -> Direction.NORTH_EAST
        Direction.NORTH_EAST -> Direction.NORTH
    }

fun Int.toDirection() : Direction =
    when(this) {
        1 -> Direction.NORTH
        2 -> Direction.NORTH_WEST
        3 -> Direction.WEST
        4 -> Direction.SOUTH_WEST
        5 -> Direction.SOUTH
        6 -> Direction.SOUTH_EAST
        7 -> Direction.EAST
        8 -> Direction.NORTH_EAST
        else -> throw IllegalArgumentException("방향은 1~8 까지의 숫자로 표현됩니다. 입력한 숫자는 : $this")
    }

interface Fish {
    var direction : Direction
}

class Mackerel(val number : Byte, override var direction : Direction) : Fish {
    constructor(other : Mackerel) : this(other.number, other.direction)

    fun rotate45() {
        direction = direction.rotate45()
    }
}

class Shark(override var direction: Direction) : Fish {
    constructor(other : Shark) : this(other.direction)
}

class Space {
    private val board : Array<Array<Fish?>>
    private val locationOfMackerels : HashMap<Byte, Location>
    private var locationOfShark : Location
    private var theSumOfTheNumbersEatenByTheShark : Int

    fun putMackerel(row : Byte, col : Byte, mackerel : Mackerel) {
        board[row.toInt()][col.toInt()] = mackerel
        locationOfMackerels[mackerel.number] = Location(row, col)
    }

    fun takeMackerel(row : Byte, col : Byte) : Mackerel {
        val fish = board[row.toInt()][col.toInt()]
        if(fish == null) throw IllegalStateException("고등어가 아닙니다. $fish")
        board[row.toInt()][col.toInt()] = null
        fish as Mackerel
        locationOfMackerels.remove(fish.number)
        return fish
    }

    constructor(lines : Array<String>) {
        locationOfMackerels = HashMap<Byte, Location>()
        board = Array<Array<Fish?>>(SIZE) { row ->
            val line = lines[row]
            val ss = line.split(Regex("\\s+"))
            Array<Fish?>(SIZE) { col ->
                val number = ss[col * 2].toByte()
                val direction = ss[col * 2 + 1].toInt().toDirection()
                locationOfMackerels[number] = Location(row.toByte(), col.toByte())
                Mackerel(number, direction)
            }
        }

        val mackerel = takeMackerel(0, 0)
        locationOfShark = Location(0, 0)
        board[0][0] = Shark(mackerel.direction)
        theSumOfTheNumbersEatenByTheShark = mackerel.number.toInt()
    }

    constructor(other : Space) {
        board = Array<Array<Fish?>>(SIZE) { row ->
            val array = other.board[row]
            Array<Fish?>(SIZE) { col ->
                val t = when(val fish = array[col]) {
                    is Mackerel -> Mackerel(fish)
                    is Shark -> Shark(fish)
                    null -> null
                    else -> throw IllegalStateException("이상한 물건이 있습니다. $fish")
                }
                t
            }
        }
        locationOfMackerels = HashMap<Byte, Location>(other.locationOfMackerels)
        locationOfShark = Location(other.locationOfShark)
        theSumOfTheNumbersEatenByTheShark = other.theSumOfTheNumbersEatenByTheShark
    }

    fun getAnswer() : Int = theSumOfTheNumbersEatenByTheShark

    private fun swap(row1 : Byte, col1 : Byte, row2 : Byte, col2 : Byte) {
        val fish1 = board[row1.toInt()][col1.toInt()]
        val fish2 = board[row2.toInt()][col2.toInt()]

        if(fish1 != null && fish2 != null) {
            val mackerel1 = takeMackerel(row1, col1)
            val mackerel2 = takeMackerel(row2, col2)
            putMackerel(row1, col1, mackerel2)
            putMackerel(row2, col2, mackerel1)
        } else if(fish1 == null && fish2 != null) {
            putMackerel(row1, col1, takeMackerel(row2, col2))
        } else if(fish1 != null && fish2 == null) {
            putMackerel(row2, col2, takeMackerel(row1, col1))
        } else {
            //no operation
        }
    }

    private fun isValid(location : Location) : Boolean {
        return (location.row in 0 until SIZE) && (location.col in 0 until SIZE)
    }

    private fun isValid(row : Byte, col : Byte) : Boolean {
        return (row in 0 until SIZE) && (col in 0 until SIZE)
    }

    private fun moveMackerel(number : Byte) {
        val location = locationOfMackerels[number] ?: return
        val mackerel = board[location.row.toInt()][location.col.toInt()] ?: return

        mackerel as Mackerel
        for(i in 0..8) {
            val nextLocation = location.oneStep(mackerel.direction)
            if(isValid(nextLocation) && nextLocation != locationOfShark) {
                swap(location.row, location.col, nextLocation.row, nextLocation.col)
                break
            }
            mackerel.rotate45()
        }
    }

    fun moveMackerels() {
        for(number in 1..(SIZE * SIZE)) moveMackerel(number.toByte())
    }

    fun thePlaceWhereSharkCanMove() : List<Location> {
        val shark = board[locationOfShark.row.toInt()][locationOfShark.col.toInt()] as Shark
        val list = ArrayList<Location>()
        var row = locationOfShark.row
        var col = locationOfShark.col

        when(shark.direction) {
            Direction.NORTH -> {
                while(true) {
                    if(isValid(--row, col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.NORTH_WEST -> {
                while(true) {
                    if(isValid(--row, --col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.WEST -> {
                while(true) {
                    if(isValid(row, --col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.SOUTH_WEST -> {
                while(true) {
                    if(isValid(++row, --col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.SOUTH -> {
                while(true) {
                    if(isValid(++row, col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.SOUTH_EAST -> {
                while(true) {
                    if(isValid(++row, ++col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.EAST -> {
                while(true) {
                    if(isValid(row, ++col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }

            Direction.NORTH_EAST -> {
                while(true) {
                    if(isValid(--row, ++col)) {
                        val fish = board[row.toInt()][col.toInt()]
                        if(fish is Mackerel) list.add(Location(row, col))
                    }
                    else break
                }
            }
        }
        return list
    }

    private fun takeShark() : Shark {
        val shark = board[locationOfShark.row.toInt()][locationOfShark.col.toInt()] as Shark
        board[locationOfShark.row.toInt()][locationOfShark.col.toInt()] = null
        return shark
    }

    private fun putShark(row : Byte, col : Byte, shark : Shark) {
        board[row.toInt()][col.toInt()] = shark
        locationOfShark = Location(row, col)
    }

    fun sharkMoveAndThenEatMackerel(location : Location) {
        val shark = takeShark()
        val mackerel = takeMackerel(location.row, location.col)

        theSumOfTheNumbersEatenByTheShark += mackerel.number
        shark.direction = mackerel.direction

        putShark(location.row, location.col, shark)
    }
}

fun main() {
    val lines = Array(SIZE) { readln() }

    val space = Space(lines)
    println(bfs(space))
}

/*
너비우선탐색 알고리즘입니다.
 */
fun bfs(space : Space) : Int {
    var answer = 0

    val queue = ArrayDeque<Space>()
    queue.addLast(space)

    while(queue.isNotEmpty()) {
        val s = queue.removeFirst()
        s.moveMackerels()
        val places = s.thePlaceWhereSharkCanMove()
        if(places.isEmpty()) {
            val t = s.getAnswer()
            if(t > answer) answer = t
        }

        for(place in places) {
            val otherSpace = Space(s)
            otherSpace.sharkMoveAndThenEatMackerel(place)
            queue.addLast(otherSpace)
        }
    }
    return answer
}