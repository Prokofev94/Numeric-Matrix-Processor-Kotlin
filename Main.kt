package processor

import kotlin.math.pow

class Matrix(
    private val rows: Int,
    private val columns: Int,
    private val matrix: List<List<Double>>
) {
    private val exception = Exception("The operation cannot be performed.")

    fun plus(matrix: Matrix): Matrix {
        if (this.rows != matrix.rows || this.columns != matrix.columns) throw exception
        return (List(rows) { this.matrix[it].zip(matrix.matrix[it]) { x, y -> x + y } }).toMatrix()
    }

    fun multiply(number: Double) = (List(rows) { row -> this.matrix[row].map { it * number } }).toMatrix()

    fun multiply(other: Matrix): Matrix {
        if (this.columns != other.rows) throw exception
        val result = List(this.rows) { MutableList(other.columns) { 0.0 } }
        for (i in 0 until this.rows) {
            for (j in 0 until other.columns) {
                var element = 0.0
                for (k in 0 until this.columns) {
                    element += this.matrix[i][k] * other.matrix[k][j]
                }
                result[i][j] = element
            }
        }
        return result.toMatrix()
    }

    fun transpose(mode: Int): Matrix {
        if (rows != columns) throw exception
        val result = List(rows) { MutableList(columns) { 0.0 } }
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                result[i][j] = when (mode) {
                    1 -> matrix[j][i]
                    2 -> matrix[columns - j - 1][rows - i - 1]
                    3 -> matrix[i][columns - j - 1]
                    4 -> matrix[rows - i - 1][j]
                    else -> getMinor(i, j).determinant() * (-1.0).pow(i + j)
                }

            }
        }
        return result.toMatrix()
    }

    fun determinant(): Double {
        if (rows != columns) throw exception
        if (rows == 1) return matrix[0][0]
        var result = 0.0
        for (i in 0 until rows) {
            val minor = getMinor(n = i)
            result += minor.determinant() * matrix[0][i] * (-1.0).pow(i)
        }
        return result
    }

    fun inverse(): Matrix {
        val det = determinant()
        if (det == 0.0) throw exception
        val inverse = transpose(0).transpose(1)
        return inverse.multiply(det.pow(-1))
    }

    private fun getMinor(m: Int = 0, n: Int) =
        (matrix.subList(0, m) + matrix.subList(m + 1, matrix.size))
            .map { it.subList(0, n) + it.subList(n + 1, it.size) }
            .toMatrix()

    override fun toString() = matrix.joinToString("\n") { it.joinToString(" ") }
}


fun main() {
    menu()
}

fun menu() {
    while (true) {
        println("""
            1. Add matrices
            2. Multiply matrix by a constant
            3. Multiply matrices
            4. Transpose matrix
            5. Calculate a determinant
            6. Inverse matrix
            0. Exit
            Your choice: """.trimIndent()
        )
        when (readln()) {
            "1" -> addMatrices()
            "2" -> multiplyMatrixByConst()
            "3" -> multiplyMatrices()
            "4" -> transposeMatrix()
            "5" -> calculateDeterminant()
            "6" -> inverseMatrix()
            "0" -> return
        }
        println()
    }
}

fun addMatrices() {
    val matrix1 = createMatrix(" first")
    val matrix2 = createMatrix(" second")
    printResult(matrix1.plus(matrix2))
}

fun multiplyMatrixByConst() {
    val matrix = createMatrix("")
    println("Enter constant:")
    val number = readln().toDouble()
    printResult(matrix.multiply(number))
}

fun multiplyMatrices() {
    val matrix1 = createMatrix(" first")
    val matrix2 = createMatrix(" second")
    printResult(matrix1.multiply(matrix2))
}

fun transposeMatrix() {
    println("""
        1. Main diagonal
        2. Side diagonal
        3. Vertical line
        4. Horizontal line
        Your choice: """.trimIndent())
    val mode = readln().toInt()
    val matrix = createMatrix("")
    printResult(matrix.transpose(mode))
}

fun calculateDeterminant() {
    val matrix = createMatrix("")
    printResult(matrix.determinant())
}

fun inverseMatrix() {
    val matrix = createMatrix("")
    printResult(matrix.inverse())
}

fun readIntLine() = readln().split(' ').map(String::toInt)

fun readDoubleLine() = readln().split(' ').map(String::toDouble)

fun List<List<Double>>.toMatrix() = Matrix(this.size, this[0].size, this)

fun createMatrix(name: String): Matrix {
    println("Enter size of$name matrix:")
    val (m, n) = readIntLine()
    println("Enter matrix:")
    return Matrix(m, n, (List(m) { readDoubleLine() }))
}

fun printResult(result: Any) = println("The result is:\n$result")