package org.example.p21608.try01

/*
    21608번 : 상어 초등학교
    출처 : https://www.acmicpc.net/problem/21608
 */

/**
 * 배열의 위치를 표현하는 클래스
 *
 * @param
 *  row : 행 번호
 *  col : 열 번호
 */
class Location(val row : Byte, val col : Byte)

class Student(val number : Short, val theNumbersOfFavoriteFriends : Set<Short>)

/**
 * @param
 *  n x n 크기의 교실이 있다.
 *  students 선생님이 자리를 배정해야 하는 학생들
 */
class Classroom(val n : Short, val students : List<Student>) {
    companion object {
        val DX = arrayOf(0, 0, -1, 1)       // 상하좌우
        val DY = arrayOf(-1, 1, 0, 0)       // 상하좌우
    }

    /**
     * 선생님이 학생을 차례대로 배치할 장소
     * 처음에는 아무도 없다
     */
    private val grid = Array(n.toInt()) { Array<Short?>(n.toInt()) { null } }

    /**
     * grid 배열에서 허용되는 위치이면 참을 반환합니다.
     */
    private fun isValidLocation(row : Int, col : Int) : Boolean = (row in 0 until n) && (col in 0 until n)

    /**
     * 기억의 한 조각
     *
     * @param
     *  theNumberOfSquaresAdjacentToYourFavoriteStudent : 비어있는 칸 중에서 좋아하는 학생이 인접한 칸의 수
     *  TheNumberOfEmptySquaresAmongAdjacentSquares : 인접한 칸 중에 비어 있는 칸의 수
     *  location : 현제의 위치 정보
     */
    class Piece(
        val theNumberOfSquaresAdjacentToYourFavoriteStudent : Byte,
        val theNumberOfEmptySquaresAmongAdjacentSquares : Byte,
        val location : Location
    )

    private inner class Teacher {
        /**
         * 현제 위치와 학생 정보를 바탕으로 기억의 한 조각을 만듭니다.
         *
         * @param
         *  student : 학생
         *  location : 현제 위치
         */
        private fun check(student : Student, location : Location) : Piece {
            var theNumberOfSquaresAdjacentToYourFavoriteStudent : Byte = 0  // 비어있는 칸 중에서 좋아하는 학생이 인접한 칸의 수
            var theNumberOfEmptySquaresAmongAdjacentSquares : Byte = 0      // 인접한 칸 중에 비어 있는 칸의 수

            for(i in 0 until DX.size) {
                val r = location.row + DY[i]
                val c = location.col + DX[i]

                if(isValidLocation(r, c)) {
                    if(grid[r][c] == null) theNumberOfEmptySquaresAmongAdjacentSquares++
                    else {
                        if(student.theNumbersOfFavoriteFriends.contains(grid[r][c])) theNumberOfSquaresAdjacentToYourFavoriteStudent++
                    }
                }
            }
            return Piece(theNumberOfSquaresAdjacentToYourFavoriteStudent, theNumberOfEmptySquaresAmongAdjacentSquares, location)
        }

        /**
         * 학생의 자리를 배정합니다.
         *
         * 기본적인 알고리즘을 정렬입니다.
         * 비어있는 모든 자리에 대해서 Piece(기억의 한 조각)을 만든 후 이것을 정렬해서 하나의 값을 선택합니다.
         */
        private fun assignStudentSeats(student : Student) : Location {
            val memory = ArrayList<Piece>()

            for(row in 0 until n) {
                for(col in 0 until n) {
                    if(grid[row][col] == null) memory.add(check(student, Location(row.toByte(), col.toByte())))
                }
            }

            memory.sortWith { a, b ->
                if(a.theNumberOfSquaresAdjacentToYourFavoriteStudent > b.theNumberOfSquaresAdjacentToYourFavoriteStudent) return@sortWith 1
                else if(a.theNumberOfSquaresAdjacentToYourFavoriteStudent == b.theNumberOfSquaresAdjacentToYourFavoriteStudent) {
                    if(a.theNumberOfEmptySquaresAmongAdjacentSquares > b.theNumberOfEmptySquaresAmongAdjacentSquares) return@sortWith 1
                    else if(a.theNumberOfEmptySquaresAmongAdjacentSquares == b.theNumberOfEmptySquaresAmongAdjacentSquares) {
                        if(a.location.row < b.location.row) return@sortWith 1
                        else if(a.location.row == b.location.row) {
                            if(a.location.col < b.location.col) return@sortWith 1
                            else if(a.location.col == b.location.col) return@sortWith 0
                            else return@sortWith -1
                        } else return@sortWith -1
                    } else return@sortWith -1
                } else return@sortWith -1
            }

            val result = memory.lastOrNull() ?: throw IllegalStateException("선택할 수 있는 위치가 없습니다.")
            return result.location
        }

        /**
         * 모든 학생의 자리를 배정합니다.
         */
        fun assignSeatsToAllStudents() {
            for(student in students) {
                val location = assignStudentSeats(student)
                grid[location.row.toInt()][location.col.toInt()] = student.number
            }
        }
    } // of Teacher

    init {
        Teacher().assignSeatsToAllStudents()
    }

    /**
     * @param
     *  row : 행의 번호
     *  col : 열의 번호
     *
     * @return
     *  (row, col)의 위치에 앉아 있는 학생의 만족도를 반환 합니다.
     */
    private fun calculateSatisfaction(row : Int, col : Int) : Int {
        val number = grid[row][col]     // 현제 (row, col) 위치에 앉아 있는 학생의 번호
        val student = students.lastOrNull { it.number == number } ?: throw IllegalStateException("학생이 존재하지 않습니다.")
        var count : Int = 0     // 좋아하는 학생 수

        for(i in 0 until DX.size) {
            val r = row + DY[i]
            val c = col + DX[i]

            if(isValidLocation(r, c)) {
                val numberOfStudent = grid[r][c]    // (row, col)에 인접한 학생의 번호
                if(student.theNumbersOfFavoriteFriends.contains(numberOfStudent)) count++
            }
        }

        return when(count) {
            0 -> 0
            1 -> 1
            2 -> 10
            3 -> 100
            4 -> 1000
            else -> throw IllegalStateException("인접한 학생 중 좋아하는 학생 수는 0~4까지 여야 합니다.")
        }
    }

    /**
     * @return
     *  만족도의 합을 반환합니다.
     */
    fun calculateSumOfSatisfaction() : Int {
        var result = 0

        for(row in 0 until n) {
            for(col in 0 until n) {
                result += calculateSatisfaction(row, col)
            }
        }
        return result
    }
}

fun main() {
    val n = readln().toShort()
    val students = ArrayList<Student>()
    for(i in 1..n * n) {
        val numbers = readln().split(Regex("\\s+")).map { it.toShort() }
        val set = HashSet<Short>()
        for(j in 1..4) set.add(numbers[j])
        val student = Student(numbers[0], set)
        students.add(student)
    }

    println("${Classroom(n, students).calculateSumOfSatisfaction()}")
}
