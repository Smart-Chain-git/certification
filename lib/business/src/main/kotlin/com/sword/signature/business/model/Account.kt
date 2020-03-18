package com.sword.signature.business.model

import java.util.*

class Account(
        val id: String,
        val login: String,
        val email: String,
        val password: String,
        val fullName: String?
) {
}
