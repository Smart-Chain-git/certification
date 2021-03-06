package com.sword.signature.model.mapper

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.sword.signature.common.criteria.FileCriteria
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.model.entity.QJobEntity
import com.sword.signature.model.entity.QTreeElementEntity
import java.time.ZoneOffset

fun FileCriteria.toPredicate(): Predicate {
    val predicates = ArrayList<Predicate>()

    val qTreeElementEntity = QTreeElementEntity("treeElementEntity")

    predicates.add(qTreeElementEntity.type.eq(TreeElementType.LEAF))

    if (id?.isNotBlank() == true) {
        predicates.add(qTreeElementEntity.id.eq(id))
    }

    if (name?.isNotBlank() == true) {
        predicates.add(qTreeElementEntity.metadata.fileName.containsIgnoreCase(name))
    }

    if (hash?.isNotBlank() == true) {
        predicates.add(qTreeElementEntity.hash.equalsIgnoreCase(hash))
    }

    if (jobId?.isNotBlank() == true) {
        predicates.add(qTreeElementEntity.jobId.eq(jobId))
    }

    if (jobIds != null) {
        predicates.add(qTreeElementEntity.jobId.`in`(jobIds))
    }

    return andTogetherPredicates(predicates)
}

fun JobCriteria.toPredicate(): Predicate {
    val predicates = ArrayList<Predicate>()
    val qJobEntity = QJobEntity("blop")

    if (id?.isNotBlank() == true) {
        predicates.add(
            qJobEntity.id.eq(id)
        )
    }


    if (accountId?.isNotBlank() == true) {
        predicates.add(
            qJobEntity.userId.eq(accountId)
        )
    }

    if (flowName?.isNotBlank() == true) {
        predicates.add(
            qJobEntity.flowName.containsIgnoreCase(flowName)
        )
    }

    if (dateStart != null) {
        predicates.add(
            qJobEntity.createdDate.after(dateStart!!.atStartOfDay().atOffset(ZoneOffset.UTC))
        )
    }

    if (dateEnd != null) {
        predicates.add(
            qJobEntity.createdDate.before(dateEnd!!.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC))
        )
    }

    if (channelName?.isNotBlank() == true) {
        predicates.add(
            qJobEntity.channelName.containsIgnoreCase(channelName)
        )
    }

    if(jobState != null) {
        predicates.add(
            qJobEntity.state.eq(jobState)
        )
    }

    return andTogetherPredicates(predicates)
}

fun TreeElementCriteria.toPredicate(): Predicate {

    val predicates = ArrayList<Predicate>()

    val qTreeElementEntity = QTreeElementEntity("blop")

    if (this.notId?.isNotBlank() == true) {
        predicates.add(
            qTreeElementEntity.id.ne(this.notId)
        )
    }

    if (this.parentId?.isNotBlank() == true) {
        predicates.add(
            qTreeElementEntity.parentId.eq(this.parentId)
        )
    }

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
