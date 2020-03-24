package com.sword.signature.greeting

data class GreetingResponse (
    val message: String
)

data class GreetingRequest (
    val documentHash: String
)