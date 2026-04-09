package university.hits.tsugeo.core.domain.classes

import university.hits.tsugeo.core.domain.interfaces.node.IBaseNode
import university.hits.tsugeo.core.domain.interfaces.node.INodeFactory

open class BaseNode(
    override val isAvailable: Boolean
): IBaseNode {
    companion object: INodeFactory<BaseNode> {
        override fun create(isAvailable: Boolean): BaseNode =
            BaseNode(isAvailable)
    }
}