package com.sword.signature.business.model

import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType


sealed class TreeElement(
    val id: String,
    val hash: String,
    val parentId: String?,
    val position: TreeElementPosition?,
    val jobId: String
) {

    class LeafTreeElement(
        id: String,
        hash: String,
        parentId: String? = null,
        position: TreeElementPosition? = null,
        jobId: String,
        val fileName: String
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, jobId = jobId)

    class NodeTreeElement(
        id: String,
        hash: String,
        parentId: String? = null,
        position: TreeElementPosition? = null,
        jobId: String
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, jobId = jobId)
}
