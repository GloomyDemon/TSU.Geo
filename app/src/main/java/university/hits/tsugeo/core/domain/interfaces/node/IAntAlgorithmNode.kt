package university.hits.tsugeo.core.domain.interfaces.node

interface IAntAlgorithmNode: IBaseNode {
    val pheromoneToNode: Map<IBaseNode, Float?>
}