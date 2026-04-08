package university.hits.tsugeo.core.domain.classes

/**
 * Один шаг работы алгоритма A*.
 *
 * Current – узел, который извлечён из очереди open на этом шаге (может быть null, если алгоритм завершён).
 * Opened – координаты узлов, которые были добавлены в open на этом шаге.
 * Updated – координаты узлов, для которых улучшился путь (уменьшился g), но они уже были в open.
 * Closed – координаты узлов, которые на этом шаге помечены как Checked (закрытые).
 * pathIfFound – финальный путь от start до goal, если на этом шаге цель достигнута.
 */
data class AStarStep(
    val current: Vector2?,
    val opened: List<Vector2>,
    val updated: List<Vector2>,
    val closed: List<Vector2>,
    val pathIfFound: List<Vector2>?
)