package com.sword.signature.business.service.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import org.springframework.data.domain.Pageable

fun <T> Flow<T>.paginate(pageable: Pageable) =if (pageable.isUnpaged) {
    this
} else {
    drop(pageable.offset.toInt()).take(pageable.pageSize)
}