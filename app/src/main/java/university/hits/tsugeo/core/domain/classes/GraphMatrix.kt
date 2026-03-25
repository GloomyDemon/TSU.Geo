    package university.hits.tsugeo.core.domain.classes

    import university.hits.tsugeo.core.domain.enums.Direction
    import java.util.BitSet

    class GraphMatrix(width: Int, height: Int?, bits: BitSet?) {
        private var _matrix: Array<Array<MatrixNode>>
        val matrix: Array<Array<MatrixNode>>
            get() = _matrix

        init {
            if (bits != null && bits.size() != width * (height ?: 1)) {
                throw IllegalStateException("BitSet size ${bits.length()} != ${width}x${height}")
            }
            _matrix = Array(width) { x ->
                Array(height ?: 1) { y ->
                    MatrixNode(x, y, bits?.get(x * width + y) ?: true)
                }
            }
            val width = _matrix.size
            val height = _matrix.firstOrNull()?.size ?: 1

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val node = _matrix[x][y]
                    for (direction in Direction.entries) {
                        val (nx, ny) = direction.translate(x, y)
                        if (nx !in 0..<width || ny !in 0..<height) {
                            continue
                        }
                        node.neighbours.add(_matrix[nx][ny])
                        node.neighbourByDirection[direction] = _matrix[nx][ny]
                        node.pheromoneToNode[_matrix[nx][ny]] = 0f
                    }
                }
            }
        }
    }