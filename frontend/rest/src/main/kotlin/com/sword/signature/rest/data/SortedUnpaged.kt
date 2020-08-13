package com.sword.signature.rest.data

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class SortedUnpaged(private val sort: Sort) : Pageable {

    override fun isPaged() = false
    override fun previousOrFirst() = this
    override fun next() = this
    override fun hasPrevious() = false
    override fun getSort() = sort

    override fun getPageSize(): Int {
        throw UnsupportedOperationException()
    }

    override fun getPageNumber(): Int {
        throw UnsupportedOperationException()
    }

    override fun getOffset(): Long {
        throw UnsupportedOperationException()
    }

    override fun first() = this
}
