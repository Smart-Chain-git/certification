package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class Algorithm(
    val id: String,
    val name: String,
    val digestLength: Int
)