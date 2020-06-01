package com.sword.signature.tezos.reader.tzindex.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class TzBlock(
    @JsonProperty("hash")
    val hash: String,
    @JsonProperty("predecessor")
    val predecessor: String,
    @JsonProperty("baker")
    val baker: String,
    @JsonProperty("height")
    val height: Long,
    @JsonProperty("time")
    val time: OffsetDateTime
)
