package university.hits.tsugeo.core.domain.interfaces.node

import university.hits.tsugeo.core.domain.classes.Vector2
import university.hits.tsugeo.core.domain.enums.Direction

interface IMatrixNode: IBaseNode, IConnectableNode {
    val axis: Vector2
    val neighbourByDirection: Map<Direction, IBaseNode?>
}