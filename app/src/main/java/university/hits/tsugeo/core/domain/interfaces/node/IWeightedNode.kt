package university.hits.tsugeo.core.domain.interfaces.node

interface IWeightedNode<T: Number>: IBaseNode {
    var weightToNode: Map<IBaseNode, T>
}