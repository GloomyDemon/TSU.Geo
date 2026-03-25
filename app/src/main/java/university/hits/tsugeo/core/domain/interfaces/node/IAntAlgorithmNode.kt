package university.hits.tsugeo.core.domain.interfaces.node

interface IAntAlgorithmNode: IBaseNode, IConnectableNode {
    val pheromoneToNode: Map<IBaseNode, Float?>
}