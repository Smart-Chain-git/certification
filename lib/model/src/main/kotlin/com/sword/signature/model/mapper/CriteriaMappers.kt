package com.sword.signature.model.mapper

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.sword.signature.common.criteria.TreeElementCriteria
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Expressions
import com.sword.signature.model.entity.QTreeElementEntity

fun TreeElementCriteria.toPredicate(): Predicate {

    val predicates = ArrayList<Predicate>()

    val qTreeElementEntity = QTreeElementEntity("blop")
    predicates.add(qTreeElementEntity.id.isNotNull) //dirty pour avoir au moin un predicat


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

private fun andTogetherPredicates(predicates: List<Predicate>): Predicate {
        val bb = BooleanBuilder()
        predicates.forEach {
            bb.and(it)
        }
      return   bb
}