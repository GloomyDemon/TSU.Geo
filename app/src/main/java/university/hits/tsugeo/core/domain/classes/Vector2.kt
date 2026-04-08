package university.hits.tsugeo.core.domain.classes

data class Vector2 (
    val x: Int,
    val y: Int
) {
    operator fun plus(other: Vector2): Vector2 =
        Vector2(x + other.x, y + other.y)

    operator fun minus(other: Vector2): Vector2 =
        Vector2(x - other.x, y - other.y)

    fun magnitude(): Double = kotlin.math.sqrt((x * x + y * y).toDouble())

}
