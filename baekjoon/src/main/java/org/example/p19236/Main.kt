package org.example.p19236

import kotlin.Array

// 물고기가 노는 공간의 크기
const val SIZE = 4

enum class Direction {
    NORTH,
    NORTH_WEST,
    WEST,
    WEST_SOUTH,
    SOUTH,
    SOUTH_EAST,
    EAST,
    EAST_NORTH
}

fun Direction.rotate45() : Direction =
    when(this) {
        Direction.NORTH -> Direction.NORTH_WEST
        Direction.NORTH_WEST -> Direction.WEST
        Direction.WEST -> Direction.WEST_SOUTH
        Direction.WEST_SOUTH -> Direction.SOUTH
        Direction.SOUTH -> Direction.SOUTH_EAST
        Direction.SOUTH_EAST -> Direction.EAST
        Direction.EAST -> Direction.EAST_NORTH
        Direction.EAST_NORTH -> Direction.NORTH
    }

fun Int.toDirection() : Direction? =
    when(this) {
        1 -> Direction.NORTH
        2 -> Direction.NORTH_WEST
        3 -> Direction.WEST
        4 -> Direction.WEST_SOUTH
        5 -> Direction.SOUTH
        6 -> Direction.SOUTH_EAST
        7 -> Direction.EAST
        8 -> Direction.EAST_NORTH
        else -> null
    }

class Location(val row : Int, val col : Int) {
    fun isValid() : Boolean = (row >= 0 && row < SIZE) && (col >= 0 && col < SIZE)
}

/**
 * @return 현제 위치(this)에서 direction 방향으로 한 칸 이동한 위치를 반환합니다.
 */
fun Location.where(direction : Direction) : Location =
    when(direction) {
        Direction.NORTH -> Location(this.row - 1, this.col)
        Direction.NORTH_WEST -> Location(this.row - 1, this.col - 1)
        Direction.WEST -> Location(this.row, this.col - 1)
        Direction.WEST_SOUTH -> Location(this.row + 1, this.col - 1)
        Direction.SOUTH -> Location(this.row + 1, this.col)
        Direction.SOUTH_EAST -> Location(this.row + 1, this.col + 1)
        Direction.EAST -> Location(this.row, this.col + 1)
        Direction.EAST_NORTH -> Location(this.row - 1, this.col + 1)
    }


interface Fish {
    var direction : Direction
}

/**
 * 상어
 */
class Shark : Fish {
    override var direction : Direction

    constructor(direction : Direction) {
        this.direction = direction
    }
}

/**
 * 고등어
 */
class Mackerel(number : Int, direction : Int) : Fish {
    val number : Byte = number.toByte()
    override var direction : Direction = direction.toDirection()
        ?: throw IllegalArgumentException("방향을 설정하는 숫자가 잘못되었습니다. $direction")

    override fun toString(): String {
        return "Mackerel $number ${direction.name}"
    }
}

/**
 * 고등어 와 상어가 있는 공간
 */
class Space {
    private val fishes : Array<Array<Fish?>>
    private val locationOfMackerels : HashMap<Byte, Location>
    private var locationOfShark : Location
    private var theSumOfTheNumbersEatenByTheShark : Int
    val mackerelsEatenByShark : MutableList<Mackerel>

    constructor(lines : Array<String>) {
        fishes = Array<Array<Fish?>>(SIZE) { Array<Fish?>(SIZE) { null } }

        for((row, line) in lines.withIndex()) {
            val ss = line.split(Regex("\\s+"))
            for(col in 0 until SIZE) {
                val number = ss[col * 2].toInt()
                val direction = ss[col * 2 + 1].toInt()
                fishes[row][col] = Mackerel(number, direction)
            }
        }

        locationOfMackerels = HashMap<Byte, Location>()

        locationOfShark = Location(0, 0)

        val t = fishes[0][0]
        theSumOfTheNumbersEatenByTheShark = (t as Mackerel).number.toInt()
        fishes[0][0] = Shark(t.direction)

        mackerelsEatenByShark = ArrayList<Mackerel>()
        mackerelsEatenByShark.add(t)
    }

    constructor(other : Space) {
        fishes = Array<Array<Fish?>>(SIZE) { other.fishes[it].copyOf<Fish?>(SIZE) }

        locationOfMackerels = HashMap<Byte, Location>()
        locationOfMackerels.putAll(other.locationOfMackerels)

        locationOfShark = Location(other.locationOfShark.row, other.locationOfShark.col)

        theSumOfTheNumbersEatenByTheShark = other.theSumOfTheNumbersEatenByTheShark
        mackerelsEatenByShark = ArrayList<Mackerel>(other.mackerelsEatenByShark)
    }

    private fun getFish(row : Int, col : Int) : Fish? = fishes[row][col]

    fun getSum() = theSumOfTheNumbersEatenByTheShark


    /**
     * Location(row1, col1) 에 있는 고등어와 Location(row2, col2) 에 있는 고등어의 위치를 바꾼다.
     */
    private fun swapMackerels(row1 : Int, col1 : Int, row2 : Int, col2 : Int) {
        val t = fishes[row1][col1]
        val u = fishes[row2][col2]

        fishes[row1][col1] = u
        if(u != null) {
            locationOfMackerels.replace((u as Mackerel).number, Location(row1, col1))
        }
        fishes[row2][col2] = t
        if(t != null) {
            locationOfMackerels.replace((t as Mackerel).number, Location(row2, col2))
        }
    }

    /**
     * Location(row, col) 위치에 있는 고등어를 고등어의 진행 방향으로 한 칸 움직인다.
     * 진행 방향에 상어가 있거나, 위치가 공간을 벗어나면, 반 시계 방향의 45도 회저한 방향으로 이동한다.
     */
    private fun moveMackerel(row : Int, col : Int) {
        val fish = fishes[row][col]
        if(fish is Shark?) throw IllegalStateException("고등어를 움직여야 하는데, 상어나 null을 움직이려 했습니다.")

        for(i in 0..7) {
            val there = Location(row, col).where(fish.direction)
            if(there.isValid()) {
                if(getFish(there.row, there.col) is Mackerel?) {
                    swapMackerels(row, col, there.row, there.col)
                    break
                }
            }
            fish.direction = fish.direction.rotate45()
        }
    }

    /**
     * 공간을 스캔해서 고등어들의 위치와 상어의 위치를 맴버 변수에 저장합니다.
     */
    private fun scanSpace() {
        locationOfMackerels.clear()
        for(row in 0 until SIZE) {
            for(col in 0 until SIZE) {
                val fish = getFish(row, col)
                if(fish is Mackerel) {
                    locationOfMackerels.put(fish.number, Location(row, col))
                } else if(fish is Shark) {
                    locationOfShark = Location(row, col)
                }
            }
        }
    }

    /**
     * 공간 안에 있는 고등어 모두를 움직인다.
     */
    fun moveMackerels() {
        scanSpace()
        for(number in 1..(SIZE * SIZE)) {
            val location = locationOfMackerels.getOrDefault(number.toByte(), null) ?: continue
            moveMackerel(location.row, location.col )
        }
    }

    /**
     * 위치가 공간 안에 있으면 참을 반환합니다.
     */
    private fun isValidLocation(row : Int, col : Int) : Boolean {
        return (row >= 0 && row < SIZE) && (col >= 0 && col < SIZE)
    }


    /**
     * 상어가 작아 먹을 수 있는 고등어의 위치를 반환합니다.
     * 위치는 여러 가지 이무로 리스트로 반환합니다.
     */
    fun thePlaceWhereSharkCanMove() : List<Location> {
        val places = ArrayList<Location>()
        val shark = fishes[locationOfShark.row][locationOfShark.col]
        var row = locationOfShark.row
        var col = locationOfShark.col

        if(shark == null) throw IllegalStateException("상어여야 하는데, 널 입니다.")
        if(shark is Mackerel) throw IllegalStateException("상어여야 하는데 고등어 입니다.")

        when(shark.direction) {
            Direction.NORTH -> {
                while(true) {
                    --row
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.NORTH_WEST -> {
                while(true) {
                    --row
                    --col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.WEST -> {
                while(true) {
                    --col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.WEST_SOUTH -> {
                while(true) {
                    ++row
                    --col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.SOUTH -> {
                while (true) {
                    ++row
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.SOUTH_EAST -> {
                while(true) {
                    ++row
                    ++col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.EAST -> {
                while(true) {
                    ++col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }

            Direction.EAST_NORTH -> {
                while(true) {
                    --row
                    ++col
                    if(!isValidLocation(row, col)) break;
                    if(fishes[row][col] is Mackerel) places.add(Location(row, col))
                }
            }
        }

        return places
    }

    /**
     * Location(row, col) 위치에 있는 고등어를 상어가 이동 후 잡아 먹는다.
     */
    fun sharkMoveAndThenEatFish(row : Int, col : Int) {
        val mackerel : Fish = fishes[row][col] ?: throw IllegalStateException("고등어가 널이 아니어야 하지만, 널 입니다.")
        val number : Byte = (mackerel as Mackerel).number

        val shark : Fish = fishes[locationOfShark.row][locationOfShark.col] ?: throw IllegalStateException("상어는 널이 아닙니다.")

        //move
        fishes[locationOfShark.row][locationOfShark.col] = null
        locationOfShark = Location(row, col)
        fishes[row][col] = shark

        //eat
        shark.direction = mackerel.direction
        theSumOfTheNumbersEatenByTheShark += number

        mackerelsEatenByShark.add(mackerel)
    }
}

fun main() {
    val lines = Array<String>(4) { "" }
    for(i in 0 until SIZE) {
        val line = readln()
        lines[i] = line
    }

    val space = Space(lines)
    println(findResult(space))
}

fun findResult(space : Space) : Int {
    space.moveMackerels()
    val places = space.thePlaceWhereSharkCanMove()
    if(places.isEmpty()) {
        for(mackerel in space.mackerelsEatenByShark) {
            print("$mackerel ")
        }
        println()
        return space.getSum()
    }

    val result = ArrayList<Int>()
    for(place in places) {
        val other = Space(space)
        other.sharkMoveAndThenEatFish(place.row, place.col)
        val t = findResult(other)
        result.add(t)
    }

    return result.stream().max { x , y ->
        if(x > y) return@max 1
        else if(x == y) return@max 0
        else return@max -1
    }.get()
}