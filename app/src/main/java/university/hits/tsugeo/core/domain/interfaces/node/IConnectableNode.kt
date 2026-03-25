package university.hits.tsugeo.core.domain.interfaces.node

interface IConnectableNode {
    val neighbours: Set<IBaseNode>
    val blockedPaths: Set<IBaseNode>
}