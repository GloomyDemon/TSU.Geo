package university.hits.tsugeo.core.domain.interfaces.node

interface IWeightedNode<T: Number>: IBaseNode, IConnectableNode {
    var weightToNode: MutableMap<IBaseNode, T>
}