package com.sword.signature.api.account

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Account(
    val id: String,
    val login: String,
    val email: String,
    val fullName: String?,
    val company: String?,
    val country: String?,
    val publicKey: String?,
    val hash: String?,
    val isAdmin: Boolean,
<<<<<<< HEAD:lib/api/src/main/kotlin/com/sword/signature/api/sign/Auth.kt
    val isActive: Boolean
=======
    val disabled: Boolean
>>>>>>> a1f29327a758f3211ce106ac96db3efe3373865a:lib/api/src/main/kotlin/com/sword/signature/api/account/Account.kt
)
