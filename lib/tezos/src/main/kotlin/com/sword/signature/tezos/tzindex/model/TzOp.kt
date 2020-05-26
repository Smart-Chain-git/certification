package com.sword.signature.tezos.tzindex.model

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
    @JsonProperty("parameters")
    val parameters: Parameters
) {
    data class Parameters(
        @JsonProperty("entrypoint")
        val entrypoint: String,
        @JsonProperty("value")
        val value: String
    )
}
