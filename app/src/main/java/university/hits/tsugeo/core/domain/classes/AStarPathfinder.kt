package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState
import university.hits.tsugeo.core.domain.enums.Direction
import university.hits.tsugeo.core.domain.interfaces.node.IAStarNode
import university.hits.tsugeo.core.domain.interfaces.node.IMatrixNode
import java.util.PriorityQueue
import kotlin.math.abs

class AStarPathfinder(
    private val map: MapMatrix<AStarNode>
) {

    fun findPath(start: Vector2, goal: Vector2): List<Vector2> {
        resetAll()

        if (!map.isAvailableForPath(start) || !map.isAvailableForPath(goal)) {
            return emptyList()
        }

        val open = createOpenQueue()

        val startNode = map.getNode(start)
        startNode.g = 0f
        startNode.h = heuristic(start, goal)
        startNode.status = AStarNodeState.Start

        open.add(start)

        while (open.isNotEmpty()) {
            val currentPos = open.poll()
            val currentNode = map.getNode(currentPos!!)

            if (currentPos == goal) {
                val goalNode = map.getNode(goal)
                goalNode.status = AStarNodeState.End
                return reconstructPath(goal)
            }

            if (currentNode.status == AStarNodeState.Checked) {
                continue
            }

            currentNode.status = AStarNodeState.Checked

            for (nextPos in neighbours(currentPos)) {
                if (!map.isAvailableForPath(nextPos)) continue

                val nextNode = map.getNode(nextPos)

                val tentativeG = currentNode.g + cost(currentPos, nextPos)

                if (tentativeG < nextNode.g) {
                    nextNode.previous = currentNode
                    nextNode.g = tentativeG
                    nextNode.h = heuristic(nextPos, goal)

                    if (nextNode.status == AStarNodeState.Unchecked) {
                        nextNode.status = AStarNodeState.Waiting
                        open.add(nextPos)
                    }
                }
            }
        }

        return emptyList()
    }

    fun runWithSteps(start: Vector2, goal: Vector2): Sequence<AStarStep> = sequence {
        resetAll()

        if (!map.isAvailableForPath(start) || !map.isAvailableForPath(goal)) {
            yield(
                AStarStep(
                    current = null,
                    opened = emptyList(),
                    updated = emptyList(),
                    closed = emptyList(),
                    pathIfFound = null
                )
            )
            return@sequence
        }

        val open = createOpenQueue()

        val startNode = map.getNode(start)
        startNode.g = 0f
        startNode.h = heuristic(start, goal)
        startNode.status = AStarNodeState.Start

        open.add(start)

        while (open.isNotEmpty()) {
            val currentPos = open.poll()
            val currentNode = map.getNode(currentPos!!)

            if (currentNode.status == AStarNodeState.Checked) {
                // Уже был закрыт – пропустим.
                continue
            }

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
                            open.add(nextPos)
                            openedOnStep.add(nextPos)
                        }

                        wasWaiting -> {
                            open.add(nextPos)
                            updatedOnStep.add(nextPos)
                        }
                    }
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
                current = null,
                opened = emptyList(),
                updated = emptyList(),
                closed = emptyList(),
                pathIfFound = null
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

    private fun createOpenQueue(): PriorityQueue<Vector2> {
        return PriorityQueue { p1: Vector2, p2: Vector2 ->
            val n1 = map.getNode(p1)
            val n2 = map.getNode(p2)

            val f1 = n1.f
            val f2 = n2.f

            val cmpF = f1.compareTo(f2)
            if (cmpF != 0) {
                cmpF
            } else {
                n1.h.compareTo(n2.h)
            }
        }
    }

    private fun heuristic(a: Vector2, b: Vector2): Float {
        return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
    }

    private fun cost(from: Vector2, to: Vector2): Float {
        return (to - from).magnitude().toFloat()
    }

    private fun neighbours(pos: Vector2): Sequence<Vector2> = sequence {
        for (dir in Direction.entries) {
            val neighbour = Vector2(pos.x + dir.vec.x, pos.y + dir.vec.y)
            yield(neighbour)
        }
    }

    private fun reconstructPath(goal: Vector2): List<Vector2> {
        val path = mutableListOf<Vector2>()

        var currentNode: IAStarNode? = map.getNode(goal)
        while (currentNode != null) {
            val matrixNode = currentNode as? IMatrixNode
                ?: break

            path.add(matrixNode.axis)

            val prev = currentNode.previous as? IAStarNode
            currentNode = prev
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
