package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckRequest(
        val documentHash: String,
        val proof: String?
)
