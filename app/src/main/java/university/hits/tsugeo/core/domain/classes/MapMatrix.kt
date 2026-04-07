package university.hits.tsugeo.core.domain.classes

import android.content.Context
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode

class MapMatrix<T : IBaseNode>(
    private val context: Context,
    private val chunkSize: Vector2,
    private val factory: (Boolean) -> T,
) {
    private val chunks = HashMap<Vector2, Chunk<T>>()
    private val visibleChunks = HashSet<Vector2>()
    private val pinnedChunks = HashSet<Vector2>()

    fun pin(axis: Vector2) {
        pinnedChunks.add(axis)
    }

    fun unpin(axis: Vector2) {
        pinnedChunks.remove(axis)
    }

    fun updateVisible(newVisible: Set<Vector2>) {
        visibleChunks.clear()
        visibleChunks.addAll(newVisible)

        for (axis in newVisible) {
            getOrLoadChunk(axis)
        }

        val toRemove = chunks.keys.filter { it !in visibleChunks && it !in pinnedChunks }
        for (axis in toRemove) {
            chunks.remove(axis)
        }
    }

    fun getNode(global: Vector2): T {
        val chunkAxis = Vector2(
            global.x.floorDiv(chunkSize.x),
            global.y.floorDiv(chunkSize.y)
        )
        val localAxis = Vector2(
            global.x - chunkAxis.x * chunkSize.x,
            global.y - chunkAxis.y * chunkSize.y
        )
        return getOrLoadChunk(chunkAxis)[localAxis]
    }

    private fun getOrLoadChunk(axis: Vector2): Chunk<T> {
        return chunks.getOrPut(axis) {
            Chunk(context, axis, chunkSize, factory)
        }
    }
}