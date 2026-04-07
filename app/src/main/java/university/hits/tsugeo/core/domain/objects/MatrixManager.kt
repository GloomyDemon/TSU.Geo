package university.hits.tsugeo.core.domain.objects

import java.nio.ByteBuffer
import java.nio.ByteOrder

object MatrixManager {
    fun encode(matrix: Array<BooleanArray>): ByteArray {
        val rows = matrix.size
        val cols = matrix.firstOrNull()?.size ?: 0

        require (matrix.all { it.size == cols }) {
            "Matrix must be rectangular"
        }

        val totalBits = rows.toLong() * cols.toLong()
        require (totalBits <= Int.MAX_VALUE.toLong()) {
            "Matrix too large: $rows × $cols"
        }

        val bytesNeeded = ((totalBits + 7) / 8).toInt()
        val buffer = ByteBuffer.allocate(8 + bytesNeeded).order(ByteOrder.BIG_ENDIAN)

        buffer.putInt(rows)
        buffer.putInt(cols)

        var currentByte = 0
        var bitCountInByte = 0

        for (bitIndex in 0 until totalBits.toInt()) {
            val row = bitIndex / cols
            val col = bitIndex % cols
            if (matrix[row][col]) {
                currentByte = currentByte or (1 shl bitCountInByte)
            }

            bitCountInByte++
            if (bitCountInByte == 8) {
                buffer.put(currentByte.toByte())
                currentByte = 0
                bitCountInByte = 0
            }
        }

        if (bitCountInByte > 0) {
            buffer.put(currentByte.toByte())
        }

        return buffer.array()
    }

    fun decode(data: ByteArray): Array<BooleanArray> {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
        require(buffer.remaining() >= 8) {
            "Matrix data is too short to contain rows and cols"
        }

        val rows = buffer.getInt()
        val cols = buffer.getInt()

        require(rows >= 0 && cols >= 0) {
            "Rows / cols negative: $rows × $cols"
        }

        val totalBits = rows.toLong() * cols.toLong()
        require(totalBits <= Int.MAX_VALUE.toLong()) {
            "Matrix too large: $rows × $cols"
        }
        val expectedBytes = ((totalBits + 7) / 8).toInt()
        val actualBytes = buffer.remaining()

        require(actualBytes == expectedBytes) {
            "Matrix size mismatch: expected $expectedBytes payload bytes, got $actualBytes"
        }

        val matrix = Array(rows) { BooleanArray(cols) }

        for (byteIdx in 0 until expectedBytes) {
            val byteVal = buffer.get().toInt() and 0xFF

            for (bitInByte in 0 until 8) {
                val bitIndex = byteIdx * 8 + bitInByte
                if (bitIndex >= totalBits.toInt()) break

                val row = bitIndex / cols
                val col = bitIndex % cols
                matrix[row][col] = (byteVal and (1 shl bitInByte)) != 0
            }
        }

        return matrix
    }
}