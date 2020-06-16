package com.sword.signature.api.algorithm

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Algorithm(
    val id: String,
    val name: String,
    val digestLength: Int
)
