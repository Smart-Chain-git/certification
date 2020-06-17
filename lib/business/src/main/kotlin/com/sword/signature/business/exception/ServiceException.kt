package com.sword.signature.business.exception

import com.sword.signature.business.model.Account

/**
 * Server errors.
 * Translated to HTTP 5xx errors.
 */
open class ServiceException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

/**
 * User input errors.
 * Translated to HTTP 4xx errors.
 */
open class UserServiceException : ServiceException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class EntityNotFoundException(name: String, id: String) : UserServiceException("The $name with id='$id' was not found.")
class AccountNotFoundException(loginOrEmail: String) :
    UserServiceException("The account with login or email '$loginOrEmail' was not found.")

class AlgorithmNotFoundException(algorithmName: String) :
    UserServiceException("The algorithm with name '$algorithmName' cannot be found.")

class PasswordTooWeakException() :
    UserServiceException("Password is too weak : must be 8 characters long, have 1 uppercase, 1 lowercase, 1 number and 1 special character")

class DuplicateException(message: String) : UserServiceException(message)
class MissingRightException(account: Account) :
    UserServiceException("The user ${account.login} doesn't have enough right to perform this operation.")
