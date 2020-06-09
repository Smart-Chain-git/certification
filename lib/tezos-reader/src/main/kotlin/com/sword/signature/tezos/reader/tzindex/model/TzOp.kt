package com.sword.signature.tezos.reader.tzindex.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class TzOp(
    @JsonProperty("hash")
    val hash: String,
    @JsonProperty("block")
    val block: String,
    @JsonProperty("time")
    val time: OffsetDateTime,
    @JsonProperty("height")
    val height: Long,
    @JsonProperty("big_map_diff")
    val bigMapDiff: List<BigMapDiff>
) {
    data class BigMapDiff(
        @JsonProperty("key")
        val key: String,
        @JsonProperty("key_hash")
        val keyHash: String,
        @JsonProperty("value")
        val value: Value,
        @JsonProperty("meta")
        val meta: Meta,
        @JsonProperty("action")
        val action: String
    ) {
        data class Value(
            @JsonProperty("0@timestamp")
            val timestamp: OffsetDateTime,
            @JsonProperty("1@address")
            val address: String
        )

        data class Meta(
            @JsonProperty("contract")
            val contract: String,
            @JsonProperty("bigmap_id")
            val bigmapId: Long,
            @JsonProperty("time")
            val time: OffsetDateTime,
            @JsonProperty("height")
            val height: Long,
            @JsonProperty("block")
            val block: String
        )
    }
}
