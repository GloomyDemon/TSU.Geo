package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState
import university.hits.tsugeo.core.domain.enums.Direction
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import kotlin.math.abs

class AStarPathfinder(
    private val map: MapMatrix<IBaseNode>
) {
    private val state = HashMap<Vector2, AStarSearchState>()

    private fun getState(pos: Vector2): AStarSearchState =
        state.getOrPut(pos) { AStarSearchState() }
    private data class OpenEntry(
        val pos: Vector2,
        val g: Float,
        val h: Float
    ) {
        val f: Float get() = g + h
    }

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

        val startState = getState(start)
        startState.g = 0f
        startState.h = heuristic(start, goal)
        startState.status = AStarNodeState.Start

        open.add(
            OpenEntry(
                start,
                startState.g,
                startState.h
            )
        )

        while (open.isNotEmpty()) {
            val currentEntry = open.poll()!!
            val currentPos = currentEntry.pos
            val currentState = getState(currentPos)

            if (currentState.status == AStarNodeState.Checked) continue

            if (!isEntryActual(currentEntry)) continue

            val openedOnStep = mutableListOf<Vector2>()
            val updatedOnStep = mutableListOf<Vector2>()
            val closedOnStep = mutableListOf<Vector2>()

            currentState.status = AStarNodeState.Checked
            closedOnStep.add(currentPos)

            if (currentPos == goal) {
                val goalState = getState(goal)
                goalState.status = AStarNodeState.End
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

                val nextState = getState(nextPos)
                val tentativeG = currentState.g + 1f

                if (tentativeG < nextState.g) {
                    val wasUnchecked = nextState.status == AStarNodeState.Unchecked
                    val wasWaiting = nextState.status == AStarNodeState.Waiting

                    nextState.previous = currentPos
                    nextState.g = tentativeG
                    nextState.h = heuristic(nextPos, goal)

                    when {
                        wasUnchecked -> {
                            nextState.status = AStarNodeState.Waiting
                            openedOnStep.add(nextPos)
                        }
                        wasWaiting -> {
                            updatedOnStep.add(nextPos)
                        }
                    }

                    open.add(OpenEntry(nextPos, nextState.g, nextState.h))
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
        state.clear()
    }

    private fun createOpenQueue() = java.util.PriorityQueue<OpenEntry> { a, b ->
        val cmpF = a.f.compareTo(b.f)
        if (cmpF != 0) cmpF else a.h.compareTo(b.h)
    }

    private fun heuristic(a: Vector2, b: Vector2): Float {
        return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
    }

    private fun neighbours(pos: Vector2): Sequence<Vector2> = sequence {
        for (dir in Direction.entries) {
            yield(Vector2(pos.x + dir.vec.x, pos.y + dir.vec.y))
        }
    }

    private fun sameFloat(a: Float, b: Float): Boolean =
        abs(a - b) < 1e-6f

    private fun isEntryActual(entry: OpenEntry): Boolean {
        val nodeState = getState(entry.pos)
        return sameFloat(entry.g, nodeState.g)
    }

    private fun reconstructPath(goal: Vector2): List<Vector2> {
        val path = mutableListOf<Vector2>()
        var current: Vector2? = goal

        while (current != null) {
            path.add(current)
            current = getState(current).previous
        }

        path.reverse()

        for (pos in path) {
            val s = getState(pos)
            if (s.status != AStarNodeState.Start && s.status != AStarNodeState.End) {
                s.status = AStarNodeState.Path
            }
        }

        return path
    }
}