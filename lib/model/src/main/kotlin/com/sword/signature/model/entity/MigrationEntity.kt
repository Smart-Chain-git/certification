package com.sword.signature.model.entity

import com.sword.signature.model.utils.VersionNumberComparator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Document(collection = "migrations")
data class MigrationEntity(
        @Id
        @Field(value = "_id")
        val id: String? = null,
        val name: String,
        val version: String,
        val hash: String,
        @CreatedDate
        val createdDate: OffsetDateTime = OffsetDateTime.now()
) {
    companion object {
        fun versionComparator() = Comparator<MigrationEntity> { migration1, migration2 ->
            VersionNumberComparator.getInstance().compare(migration1.version, migration2.version)
        }
    }
}
