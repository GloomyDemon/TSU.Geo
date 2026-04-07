package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.enums.AStarNodeState
import university.hits.tsugeo.core.domain.enums.Direction
import university.hits.tsugeo.core.domain.interfaces.node.IAStarNode
import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import university.hits.tsugeo.core.domain.interfaces.node.IMatrixNode
import university.hits.tsugeo.core.domain.interfaces.node.INodeFactory

class AStarNode(
    isAvailable: Boolean,
    override var g: Float = 0f,
    override var h: Float = 0f
) : BaseNode(isAvailable), IAStarNode, IMatrixNode {

    companion object : INodeFactory<AStarNode> {
        override fun create(isAvailable: Boolean): AStarNode =
            AStarNode(isAvailable)
    }

    override val f: Float
        get() = g + h

    override var previous: IBaseNode? = null

    override var status: AStarNodeState = AStarNodeState.Unchecked

    override var axis: Vector2 = Vector2(0, 0)

    override var neighbourByDirection: Map<Direction, IBaseNode?> = emptyMap()
}