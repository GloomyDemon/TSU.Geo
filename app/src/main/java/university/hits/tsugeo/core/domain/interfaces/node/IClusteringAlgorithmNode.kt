package university.hits.tsugeo.core.domain.interfaces.node

interface IClusteringAlgorithmNode: IBaseNode, IConnectableNode {
    var clusterId: Int?
    var distanceToCentroid: Float
}