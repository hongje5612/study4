package org.example.p19238

import java.util.Arrays
import java.util.Objects

/**
 * 문제 출처 : https://www.acmicpc.net/problem/19238
 *
 * @author
 *  조홍제
 */

const val EMPTY : Byte = 0      // 지도에서 빈 공간
const val WALL : Byte = 1       // 지도에서 벽이 있는 공간

/**
 * map 배열 위의 위치를 나타내는 클래스
 *
 * @param
 *  row : 행의 위치
 *  col : 열의 위치
 */
class Location(val row : Byte, val col : Byte) {
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

    override fun hashCode(): Int = Objects.hash(row, col)

    fun left() : Location = Location(row, col - 1)
    fun right() : Location = Location(row, col + 1)
    fun up() : Location = Location(row - 1, col)
    fun down() : Location = Location(row + 1, col)
}

/**
 * 승객을 표현하는 클래스
 *
 * @param
 *  startingPoint   : 출발 지점
 *  destination     : 목적지
 */
class Passenger(val startingPoint : Location, val destination : Location)

/**
 * 택시를 표현하는 클래스
 *
 * @param
 *  currentLocation     : 현제의 위치
 *  fuelQuantity        : 연료량
 */
class Taxi(var currentLocation : Location, var fuelQuantity: Int)

/**
 * 문제를 푸는 클래스
 *
 * @param
 *  size                : map 의 크기 (size x size)
 *  numberOfPassengers  : 승객 수
 *  fuelQuantity        : 택시의 연료량
 *  map                 : 지도 정보
 *  startingPoint       : 택시의 출발 위치
 *  passengers          : 승객의 정보
 */
class Solution(private val size : Int,
               private val numberOfPassengers : Int,
               fuelQuantity : Int,
               private val map : Array<ByteArray>,
               startingPoint: Location,
               private val passengers : HashMap<Int, Passenger>)
{
    // 택시
    private val taxi = Taxi(startingPoint, fuelQuantity)
    /*
        bfs(너비 우선 탐색)에서 사용되어지는 변수
        임의의 출발지점에서 (row, col) 위치까지의 거리를 저장하는 변수
     */
    private val distances = Array(size) { ByteArray(size) }

    private fun Location.isValid() = (row in 0 until size) && (col in 0 until size)

    /**
     * 출발점(startingPoint)에서 출발하여 임의의 위치(row, col)까지의 거리를 distances 멤버 변수에 기록한다.
     *
     * @param
     *  startingPoint   : 출발점
     */
    private fun bfs(startingPoint : Location) {
        class Piece(val location : Location, val distance : Int)

        /*
            처음에는 모던 지점의 거리를 무한대로 설정한다. 모른다는 의미이다.
            나중에서 bfs 끝나고도 무한대인 지점은 택시가 갈 수 없음을 의미한다.
         */
        for(arr in distances) {
            Arrays.fill(arr, Byte.MAX_VALUE)
        }

        val visited = Array(size) { BooleanArray(size) { false } }      // 방문하지 않았음
        val inQueue = Array(size) { BooleanArray(size) { false } }      // 큐 안에 존재 하지 않음
        val queue = ArrayDeque<Piece>()
        queue.addLast(Piece(startingPoint, 0))
        inQueue[startingPoint.row.toInt()][startingPoint.col.toInt()] = true

        while(queue.isNotEmpty()) {
            val piece = queue.removeFirst()
            var r = piece.location.row.toInt()
            var c = piece.location.col.toInt()
            distances[r][c] = piece.distance.toByte()
            visited[r][c] = true
            inQueue[r][c] = false

            val nextLocations = arrayOf(piece.location.left(), piece.location.right(), piece.location.down(), piece.location.up())

            for(nextLocation in nextLocations) {
                if(nextLocation.isValid()) {
                    r = nextLocation.row.toInt()
                    c = nextLocation.col.toInt()
                    if(!visited[r][c] && !inQueue[r][c] && map[r][c] == EMPTY) {
                        queue.addLast(Piece(nextLocation, piece.distance + 1))
                        inQueue[r][c] = true
                    }
                }
            }
        }
    }

    /**
     * Taxi로 부터 가장 가까운 승객의 정보
     *
     * @param
     *  number      : 번호
     *  distance    : 택시로 부터의 거리
     */
    private class NearestPassenger(val number : Int, val distance : Int)

    /**
     * 택시에서 가장 가까운 승객을 찾는다.
     *
     * @return
     *  NearestPassenger 를 반환한다.
     */
    private fun Taxi.nearestPassenger() : NearestPassenger? {
        bfs(currentLocation)

        var nearestPassengers = ArrayList<Int>()
        var minDistance = Byte.MAX_VALUE

        for(passenger in passengers) {
            val r = passenger.value.startingPoint.row.toInt()
            val c = passenger.value.startingPoint.col.toInt()
            val distance = distances[r][c]
            if(distance < minDistance) {
                nearestPassengers = ArrayList()
                nearestPassengers.add(passenger.key)
                minDistance = distance
            } else if(distance == minDistance) nearestPassengers.add(passenger.key)
        }

        if(minDistance == Byte.MAX_VALUE) return null       // 가까운 승객의 거리가 무한대이면 승객을 pickUp 할 수 없음

        when(nearestPassengers.size) {
            0 -> return null
            1 -> return NearestPassenger(nearestPassengers.first(), minDistance.toInt())
            else -> {
                nearestPassengers.sortWith { a, b ->
                    val passenger1 = passengers[a]
                    val passenger2 = passengers[b]
                    val r1 = passenger1!!.startingPoint.row
                    val c1 = passenger1.startingPoint.col
                    val r2 = passenger2!!.startingPoint.row
                    val c2 = passenger2.startingPoint.col

                    if(r1 > r2) return@sortWith 1
                    else if(r1 == r2) {
                        if(c1 > c2) return@sortWith 1
                        else if(c1 == c2) return@sortWith 0
                        else return@sortWith -1
                    } else return@sortWith -1
                }
                return NearestPassenger(nearestPassengers.first(), minDistance.toInt())
            }
        }
    }

    /**
     * @return
     *  승객을 태우러 갈 수 있으면 참을 반환한다.
     */
    private fun Taxi.canIPickUpPassenger(nearestPassenger : NearestPassenger) : Boolean {
        return fuelQuantity > nearestPassenger.distance
    }

    private fun Taxi.pickUpPassenger(nearestPassenger : NearestPassenger) {
        fuelQuantity -= nearestPassenger.distance
        currentLocation = passengers[nearestPassenger.number]!!.startingPoint
    }

    /**
     * @param
     *  numberOfPassenger   : 승객 번호
     *
     * @return
     *  승객을 목적지 까지 태워줄 수 있으면 거리를 반환하고, 아니며 -1 반환한다.
     */
    private fun Taxi.canITakeThePassengerToHisDestination(numberOfPassenger : Int) : Int {
        bfs(currentLocation)
        val destination = passengers[numberOfPassenger]!!.destination
        val distance = distances[destination.row.toInt()][destination.col.toInt()]
        if(distance == Byte.MAX_VALUE) return -1    // 길이 막혀서 갈 수 없으면
        return if(fuelQuantity >= distance) distance.toInt() else -1 // 연료량이 충분하면 distance를 아니면 -1를 반환한다.
    }

    /**
     * 승객을 목적지에 데려다 주다
     *
     * @param
     *  numberOfPassenger       : 승객 번호
     *  distance                : 목적지까지 거리
     */
    private fun Taxi.takePassengerToTheirDestination(numberOfPassenger : Int, distance : Int) {
        fuelQuantity += distance
        currentLocation = passengers[numberOfPassenger]!!.destination
        passengers.remove(numberOfPassenger)
    }

    /**
     * 문제에서 제시한 과정을 모의시험 해 본다.
     *
     * @return
     *  모던 과정을 마치면 택시에 남아 있는 연료량을 반환한다. 모던 과정을 마치지 못 하면 -1을 반환한다.
     */
    fun simulate() : Int {
        for(i in 0 until numberOfPassengers) {
            val nearestPassenger = taxi.nearestPassenger() ?: return -1

            if(!taxi.canIPickUpPassenger(nearestPassenger)) return -1

            taxi.pickUpPassenger(nearestPassenger)

            val distance = taxi.canITakeThePassengerToHisDestination(nearestPassenger.number)

            if(distance == -1) return -1

            taxi.takePassengerToTheirDestination(nearestPassenger.number, distance)
        }
        return taxi.fuelQuantity
    }
}

fun main() {
    // n : 지도의 크기, m : 손님의 수, fuelQuantity : 초기연료량
    val (n, m, fuelQuantity) = readln().split(Regex("\\s+")).map { it.toInt() }
    /*
        택시가 활동하는 지역의 지도 정보
        0 : 빈칸
        1 : 벽
    */
    val map = Array(n) {
        readln().split(Regex("\\s+")).map { it.toByte() }.toByteArray()
    }

    // 택시의 출발 지점
    val (r, c) = readln().split(Regex("\\s+")).map { it.toByte() }
    // r - 1, c - 1 : 1를 빼는 이유는 배열의 인덱스가 0에서 시작하기 때문입니다.
    val startingPoint = Location(r - 1, c - 1)

    // 승객 정보
    val passengers = HashMap<Int, Passenger>()
    for(number in 1..m) {
        val (r1, c1, r2, c2) = readln().split(Regex("\\s+")).map { it.toByte() }
        // 입력 받은 행과 열에서 1를 빼는 이유는 배열의 인덱스가 0에서 출발하기 때문입니다.
        passengers[number] = Passenger(Location(r1 - 1, c1 - 1), Location(r2 - 1, c2 - 1))
    }

    println("${Solution(n, m, fuelQuantity, map, startingPoint, passengers).simulate()}\n")
}