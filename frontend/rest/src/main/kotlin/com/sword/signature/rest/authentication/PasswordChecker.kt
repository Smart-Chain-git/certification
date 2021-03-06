package com.sword.signature.rest.authentication

import com.sword.signature.business.exception.PasswordTooWeakException

fun checkPassword(password: String) {
    if (password.length < 8 ||
        !password.contains(regex = "[a-z]".toRegex()) ||
        !password.contains(regex = "[A-Z]".toRegex()) ||
        !password.contains(regex = "[0-9]".toRegex()) ||
        !password.contains(regex = "[ !\"#\$%&'()*+,-./:;<=>?@^_`{|}~]".toRegex())
    ) {
        throw PasswordTooWeakException()
    }
}
