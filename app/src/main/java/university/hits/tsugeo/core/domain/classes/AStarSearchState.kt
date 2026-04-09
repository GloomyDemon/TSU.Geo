package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState

data class AStarSearchState (
    var g: Float = Float.POSITIVE_INFINITY,
    var h: Float = 0f,
    var previous: Vector2? = null,
    var status: AStarNodeState = AStarNodeState.Unchecked
) {
    val f: Float get() = g + h
}