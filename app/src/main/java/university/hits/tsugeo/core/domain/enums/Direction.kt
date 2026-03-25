package university.hits.tsugeo.core.domain.enums

enum class Direction(val dx: Int, val dy: Int) {
    Up(0, 1),
    Down(0, -1),
    Left(-1, 0),
    Right(1, 0);

    fun translate(x: Int, y: Int): Pair<Int, Int> = x + dx to y + dy
}