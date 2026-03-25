package university.hits.tsugeo.core.domain.interfaces.node

interface IClusteringAlgorithmNode: IMatrixNode {
    var clusterId: Int?
    var distanceToCentroid: Float
}