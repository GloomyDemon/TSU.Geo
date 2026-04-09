package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState
import university.hits.tsugeo.core.domain.enums.Direction
import university.hits.tsugeo.core.domain.interfaces.node.IAStarNode
import university.hits.tsugeo.core.domain.interfaces.node.IMatrixNode
import kotlin.math.abs

class AStarPathfinder(
    private val map: MapMatrix<AStarNode>
) {
    private data class OpenEntry(
        val pos: Vector2,
        val f: Float,
        val h: Float
    )

    fun findPath(start: Vector2, goal: Vector2): List<Vector2> {
        for (step in findPathWithSteps(start, goal)) {
            val path = step.pathIfFound
            if (path != null) return path
        }
        return emptyList()
    }

    private fun findPathWithSteps(start: Vector2, goal: Vector2): Sequence<AStarStep> = sequence {
        resetAll()

        if (!map.isAvailableForPath(start) || !map.isAvailableForPath(goal)) {
            yield(
                AStarStep(
                    null,
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    null
                )
            )
            return@sequence
        }

        val open = createOpenQueue()

        val startNode = map.getNode(start)
        startNode.g = 0f
        startNode.h = heuristic(start, goal)
        startNode.status = AStarNodeState.Start
        open.add(
            OpenEntry(
                start,
                startNode.f,
                startNode.h
            )
        )

        while (open.isNotEmpty()) {
            val currentEntry = open.poll()!!
            val currentPos = currentEntry.pos
            val currentNode = map.getNode(currentPos)

            if (currentNode.status == AStarNodeState.Checked) continue

            val openedOnStep = mutableListOf<Vector2>()
            val updatedOnStep = mutableListOf<Vector2>()
            val closedOnStep = mutableListOf<Vector2>()

            currentNode.status = AStarNodeState.Checked
            closedOnStep.add(currentPos)

            if (currentPos == goal) {
                val goalNode = map.getNode(goal)
                goalNode.status = AStarNodeState.End
                val path = reconstructPath(goal)

                yield(
                    AStarStep(
                        current = currentPos,
                        opened = openedOnStep,
                        updated = updatedOnStep,
                        closed = closedOnStep,
                        pathIfFound = path
                    )
                )
                return@sequence
            }

            for (nextPos in neighbours(currentPos)) {
                if (!map.isAvailableForPath(nextPos)) continue

                val nextNode = map.getNode(nextPos)
                val tentativeG = currentNode.g + cost(currentPos, nextPos)

                if (tentativeG < nextNode.g) {
                    val wasUnchecked = nextNode.status == AStarNodeState.Unchecked
                    val wasWaiting = nextNode.status == AStarNodeState.Waiting

                    nextNode.previous = currentNode
                    nextNode.g = tentativeG
                    nextNode.h = heuristic(nextPos, goal)

                    when {
                        wasUnchecked -> {
                            nextNode.status = AStarNodeState.Waiting
                            openedOnStep.add(nextPos)
                        }
                        wasWaiting -> {
                            updatedOnStep.add(nextPos)
                        }
                    }

                    open.add(OpenEntry(nextPos, nextNode.f, nextNode.h))
                }
            }

            yield(
                AStarStep(
                    current = currentPos,
                    opened = openedOnStep,
                    updated = updatedOnStep,
                    closed = closedOnStep,
                    pathIfFound = null
                )
            )
        }

        yield(
            AStarStep(
                null,
                emptyList(),
                emptyList(),
                emptyList(),
                null
            )
        )
    }

    private fun resetAll() {
        map.forEachLoadedNode { node ->
            node.g = Float.POSITIVE_INFINITY
            node.h = 0f
            node.previous = null
            node.status = AStarNodeState.Unchecked
        }
    }

    private fun createOpenQueue() = java.util.PriorityQueue<OpenEntry> { a, b ->
        val cmpF = a.f.compareTo(b.f)
        if (cmpF != 0) cmpF else a.h.compareTo(b.h)
    }

    private fun heuristic(a: Vector2, b: Vector2): Float {
        return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
    }

    private fun cost(from: Vector2, to: Vector2): Float = 1f

    private fun neighbours(pos: Vector2): Sequence<Vector2> = sequence {
        for (dir in Direction.entries) {
            yield(Vector2(pos.x + dir.vec.x, pos.y + dir.vec.y))
        }
    }

    private fun reconstructPath(goal: Vector2): List<Vector2> {
        val path = mutableListOf<Vector2>()

        var currentNode: IAStarNode? = map.getNode(goal)
        while (currentNode != null) {
            val matrixNode = currentNode as? IMatrixNode ?: break
            path.add(matrixNode.axis)
            currentNode = currentNode.previous as? IAStarNode
        }

        path.reverse()

        for (pos in path) {
            val node = map.getNode(pos)
            if (node.status != AStarNodeState.Start && node.status != AStarNodeState.End) {
                node.status = AStarNodeState.Path
            }
        }

        return path
    }
}