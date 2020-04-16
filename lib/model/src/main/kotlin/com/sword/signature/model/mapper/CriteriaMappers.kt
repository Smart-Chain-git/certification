package com.sword.signature.model.mapper

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.model.entity.QTreeElementEntity

fun TreeElementCriteria.toPredicate(): Predicate {

    val predicates = ArrayList<Predicate>()

    val qTreeElementEntity = QTreeElementEntity("blop")

    if (this.jobId?.isNotBlank() == true) {
        predicates.add(
            qTreeElementEntity.jobId.eq(this.jobId)
        )
    }

    if (this.type != null) {
        predicates.add(
            qTreeElementEntity.type.eq(this.type)
        )
    }
    return andTogetherPredicates(predicates)
}

private fun andTogetherPredicates(predicates: List<Predicate>) = BooleanBuilder().apply {
    predicates.forEach {
        and(it)
    }
}
