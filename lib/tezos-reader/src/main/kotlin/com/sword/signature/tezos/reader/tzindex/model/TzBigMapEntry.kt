package com.sword.signature.tezos.reader.tzindex.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class TzBigMapEntry(
    val key: String,
    @JsonProperty("key_hash") val keyHash: String,
    val value: Value,
    val meta: Meta
) {
    class Value(
        @JsonProperty("0@timestamp") val timestamp: OffsetDateTime,
        @JsonProperty("1@address") val address: String
    )

    class Meta(
        val contract: String,
        @JsonProperty("bigmap_id") val bigMapId: Int,
        val time: OffsetDateTime,
        val height: Long,
        val block: String
    )
}