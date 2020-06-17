package com.sword.signature.rest.data

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort


fun pagedSorted(page: Int? = null, size: Int? = null, sort: List<String>? = null) =
    if (page !== null && size !== null) {
        PageRequest.of(page, size, sortBy(sort))
    } else {
        SortedUnpaged(sortBy(sort))
    }

fun sortBy(sort: List<String>?): Sort {
    val orders = sort?.mapNotNull {
        val splits = it.split(":".toRegex(), 2)
        val direction = when {
            splits.size < 2 -> Sort.Direction.ASC
            splits[1].equals("desc", ignoreCase = true) -> Sort.Direction.DESC
            else -> Sort.Direction.ASC
        }
        Sort.Order(direction, splits[0])
    } ?: emptyList()
    return Sort.by(orders)
}