package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.Account
import com.sword.signature.model.entity.AccountEntity

fun Account.toModel() = AccountEntity(
        id = id,
        login = login,
        email = email,
        password = password,
        fullName = fullName
)
