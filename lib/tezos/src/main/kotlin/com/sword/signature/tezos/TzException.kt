package com.sword.signature.tezos

import java.lang.RuntimeException

sealed class TzException: RuntimeException {
    constructor(message: String) : super(message)

    class IndexerAccessException() : TzException("The indexer is unreachable or produced an error.")
}