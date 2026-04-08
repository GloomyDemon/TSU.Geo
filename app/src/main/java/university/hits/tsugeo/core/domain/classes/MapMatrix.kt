package university.hits.tsugeo.core.domain.classes

import android.content.Context
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import university.hits.tsugeo.core.domain.interfaces.node.IMatrixNode

class MapMatrix<T : IBaseNode>(
    private val context: Context,
    private val chunkSize: Vector2,
    private val factory: (Boolean) -> T,
) {
    private val chunks = HashMap<Vector2, Chunk<T>>()
    private val visibleChunks = HashSet<Vector2>()
    private val pinnedChunks = HashSet<Vector2>()

    private val modified = HashSet<Vector2>()

    fun pin(axis: Vector2) {
        pinnedChunks.add(axis)
    }

    fun unpin(axis: Vector2) {
        pinnedChunks.remove(axis)
    }

    fun clearPins() {
        pinnedChunks.clear()
    }

    fun isPinned(axis: Vector2): Boolean {
        return axis in pinnedChunks
    }

    fun updateVisible(newVisible: Set<Vector2>) {
        visibleChunks.clear()
        visibleChunks.addAll(newVisible)

        for (axis in newVisible) {
            this[axis]
        }

        val toRemove = chunks.keys.filter { it !in visibleChunks && it !in pinnedChunks }
        for (axis in toRemove) {
            chunks.remove(axis)
        }
    }

    fun getChunkAxis(global: Vector2): Vector2 {
        return Vector2(
            global.x.floorDiv(chunkSize.x),
            global.y.floorDiv(chunkSize.y)
        )
    }

    fun getLocalAxis(global: Vector2): Vector2 {
        val chunkAxis = getChunkAxis(global)
        return Vector2(
            global.x - chunkAxis.x * chunkSize.x,
            global.y - chunkAxis.y * chunkSize.y
        )
    }

    fun getNode(global: Vector2): T {
        val chunkAxis = getChunkAxis(global)
        val localAxis = getLocalAxis(global)
        val node = this[chunkAxis][localAxis]

        if (node is IMatrixNode) {
            if (node.axis != global) {
                node.axis = global
            }
        }

        return node
    }

    operator fun get(axis: Vector2): Chunk<T> {
        return chunks.getOrPut(axis) {
            Chunk(context, axis, chunkSize, factory)
        }
    }

    fun containsChunk(axis: Vector2): Boolean {
        return axis in chunks
    }

    fun removeChunk(axis: Vector2): Chunk<T>? {
        if (axis in pinnedChunks) return null
        return chunks.remove(axis)
    }

    fun clear() {
        chunks.clear()
        visibleChunks.clear()
        pinnedChunks.clear()
        modified.clear()
    }

    fun forEachChunk(action: (Vector2, Chunk<T>) -> Unit) {
        chunks.forEach(action)
    }

    fun forEachLoadedNode(action: (T) -> Unit) {
        for ((_, chunk) in chunks) {
            chunk.forEachNode(action)
        }
    }
    fun isModified(axis: Vector2): Boolean {
        return axis in modified
    }

    fun toggleModified(axis: Vector2) {
        if (!modified.add(axis)) {
            modified.remove(axis)
        }
    }

    fun setModified(axis: Vector2, isModified: Boolean) {
        if (isModified) {
            modified.add(axis)
        } else {
            modified.remove(axis)
        }
    }

    fun isAvailableForPath(global: Vector2): Boolean {
        val node = getNode(global)
        val base = node.isAvailable
        val modifiedHere = isModified(global)
        return if (modifiedHere) !base else base
    }
}
