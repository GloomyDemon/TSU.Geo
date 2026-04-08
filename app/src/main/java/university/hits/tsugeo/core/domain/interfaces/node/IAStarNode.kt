package university.hits.tsugeo.core.domain.interfaces.node

import university.hits.tsugeo.core.domain.enums.AStarNodeState

interface IAStarNode: IBaseNode {
    var g: Float
    var h: Float
    val f: Float
    var previous: IBaseNode?
    var status: AStarNodeState
}