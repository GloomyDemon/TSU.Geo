package university.hits.tsugeo.core.domain.interfaces.node

import university.hits.tsugeo.core.domain.enums.Direction

interface IMatrixNode: IBaseNode, IConnectableNode {
    val x: Int
    val y: Int
    val neighbourByDirection: Map<Direction, IBaseNode?>
}