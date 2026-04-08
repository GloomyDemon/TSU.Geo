package university.hits.tsugeo.core.domain.interfaces.node

interface IClusteringAlgorithmNode: IBaseNode {
    var clusterId: Int?
    var distanceToCentroid: Float
}
