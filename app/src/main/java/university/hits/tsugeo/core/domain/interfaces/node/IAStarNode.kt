package university.hits.tsugeo.core.domain.interfaces.node

import university.hits.tsugeo.core.domain.enums.AStarNodeState

interface IAStarNode: IBaseNode, IConnectableNode {
    val g: Float
    val h: Float
    val f: Float
    val previous: IBaseNode?

    val status: AStarNodeState
}