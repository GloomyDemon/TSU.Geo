package university.hits.tsugeo.core.domain.classes

import android.content.Context
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import university.hits.tsugeo.core.domain.objects.FileManager
import university.hits.tsugeo.core.domain.objects.MatrixManager

class Chunk<T : IBaseNode>(
    context: Context,
    axis: Vector2,
    val size: Vector2,
    val factory: (isAvailable: Boolean) -> T
) {
    private val data: List<List<T>>

    init {
        require(size.x >= 0 && size.y >= 0) {
            "All sizes mustn't be negative"
        }

        val paths: Array<BooleanArray> = MatrixManager.decode(
            FileManager.readRawBytes(
                context,
                "chunk_${size.x}x${size.y}_${axis.x}_${axis.y}.bin"
            )
        )

        require(
            paths.size == size.x &&
                    (paths.firstOrNull()?.size ?: 0) == size.y
        ) {
            "Matrix size mismatch: " +
                    "expected size: ${size.x} × ${size.y}, " +
                    "got: ${paths.size} × ${paths.firstOrNull()?.size}"
        }

        data = List(size.x) { i ->
            List(size.y) { j ->
                factory(paths[i][j])
            }
        }
    }

    operator fun get(axis: Vector2): T {
        require(axis.x in 0 until size.x &&
                axis.y in 0 until size.y) {
            "Runtime error. Index out of range. " +
                    "Expected: x in 0 until ${size.x}, y in 0 until ${size.y}. " +
                    "Got: x = ${axis.x}, y = ${axis.y}"
        }
        return data[axis.x][axis.y]
    }

    fun forEachNode(action: (node: T) -> Unit) {
        for (x in 0 until size.x) {
            for (y in 0 until size.y) {
                action(data[x][y])
            }
        }
    }

    fun forEachNodeIndexed(action: (axis: Vector2, node: T) -> Unit) {
        for (x in 0 until size.x) {
            for (y in 0 until size.y) {
                action(Vector2(x, y), data[x][y])
            }
        }
    }
}
