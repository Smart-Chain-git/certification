package com.sword.signature.business.exception

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
class AccountNotFoundException(loginOrEmail: String) : UserServiceException("The account with login or email '$loginOrEmail' was not found.")
class AlgorithmNotFoundException(algorithmName: String) : UserServiceException("The algorithm with name '$algorithmName' cannot be found.")