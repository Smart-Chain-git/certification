package com.sword.signature.business.service

import org.ej4tezos.api.exception.TezosException

interface TezosWriterService {

    @Throws(TezosException::class)
    fun anchorHash(rootHash: String): String
}