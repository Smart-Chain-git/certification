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
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, jobId = jobId) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is LeafTreeElement) return false
            if (!super.equals(other)) return false

            if (fileName != other.fileName) return false

            return super.equals(other)
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + fileName.hashCode()
            return result
        }
    }

    class NodeTreeElement(
        id: String,
        hash: String,
        parentId: String? = null,
        position: TreeElementPosition? = null,
        jobId: String
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, jobId = jobId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TreeElement) return false

        if (id != other.id) return false
        if (hash != other.hash) return false
        if (parentId != other.parentId) return false
        if (position != other.position) return false
        if (jobId != other.jobId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (position?.hashCode() ?: 0)
        result = 31 * result + jobId.hashCode()
        return result
    }


}
