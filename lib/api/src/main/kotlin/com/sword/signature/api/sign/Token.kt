package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Token(
        val id: String,
        val name: String
)
