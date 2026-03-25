package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState
import university.hits.tsugeo.core.domain.enums.Direction
import university.hits.tsugeo.core.domain.interfaces.node.IAStarNode
import university.hits.tsugeo.core.domain.interfaces.node.IAntAlgorithmNode
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import university.hits.tsugeo.core.domain.interfaces.node.IConnectableNode
import university.hits.tsugeo.core.domain.interfaces.node.IMatrixNode

class MatrixNode(
    override val x: Int,
    override val y: Int,
    available: Boolean = true
): IBaseNode, IConnectableNode, IMatrixNode, IAStarNode, IAntAlgorithmNode {
    private var _available: Boolean = available
    override val available: Boolean
        get() = _available

    private val _neighbours = mutableSetOf<IBaseNode>()
    override val neighbours: MutableSet<IBaseNode>
        get() = _neighbours

    private val _blockedPaths = mutableSetOf<IBaseNode>()
    override val blockedPaths: Set<IBaseNode>
        get() = _blockedPaths

    private var _neighbourByDirection =
        mutableMapOf<Direction, IBaseNode?>(
            Direction.Up to null, Direction.Down to null,
            Direction.Left to null, Direction.Right to null
        )
    override val neighbourByDirection: MutableMap<Direction, IBaseNode?>
        get() = _neighbourByDirection

    private var _g: Float = 0f
    override val g: Float
        get() = _g

    private var _h: Float = 0f
    override val h: Float
        get() = _h

    override val f: Float
        get() = g + h

    private var _previous: IBaseNode? = null
    override val previous: IBaseNode?
        get() = _previous

    private var _status = AStarNodeState.Unchecked
    override val status: AStarNodeState
        get() = _status

    private var _pheromoneToNode = mutableMapOf<IBaseNode, Float?>()
    override val pheromoneToNode: MutableMap<IBaseNode, Float?>
        get() = _pheromoneToNode
}