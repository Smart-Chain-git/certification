package com.sword.signature.tezos.reader.tzindex.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TzContract(
    @JsonProperty("address")
    val address: String,
    @JsonProperty("manager")
    val manager: String,
    @JsonProperty("height")
    val height: Long
)