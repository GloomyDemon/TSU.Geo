package university.hits.tsugeo.core.domain.interfaces.node

interface INodeFactory<T: IBaseNode> {
    fun create(isAvailable: Boolean): T
}