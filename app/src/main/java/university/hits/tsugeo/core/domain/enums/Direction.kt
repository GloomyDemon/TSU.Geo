package university.hits.tsugeo.core.domain.enums

import university.hits.tsugeo.core.domain.classes.Vector2

enum class Direction(val vec: Vector2) {
    Up(Vector2(0,-1)),
    Down(Vector2(0, 1)),
    Left(Vector2(-1, 0)),
    Right(Vector2(1, 0));
}