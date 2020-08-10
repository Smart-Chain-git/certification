package com.sword.signature.business.model

import com.sword.signature.common.enums.TreeElementPosition

sealed class TreeElement(
    val id: String,
    val hash: String,
    val parentId: String?,
    val position: TreeElementPosition?,
    val job: Job
) {

    class LeafTreeElement(
        id: String,
        hash: String,
        parentId: String? = null,
        position: TreeElementPosition? = null,
        job: Job,
        val metadata: FileMetadata
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, job = job) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as LeafTreeElement

            if (metadata != other.metadata) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + metadata.hashCode()
            return result
        }
    }

    class NodeTreeElement(
        id: String,
        hash: String,
        parentId: String? = null,
        position: TreeElementPosition? = null,
        job: Job
    ) : TreeElement(id = id, hash = hash, parentId = parentId, position = position, job = job)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeElement

        if (id != other.id) return false
        if (hash != other.hash) return false
        if (parentId != other.parentId) return false
        if (position != other.position) return false
        if (job != other.job) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (position?.hashCode() ?: 0)
        result = 31 * result + job.hashCode()
        return result
    }
}
