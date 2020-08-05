package com.sword.signature.business.exception

import java.time.OffsetDateTime

sealed class CheckException(
    val errorCode: Int,
    override val message: String
) : RuntimeException() {

    class HashNotPresent(val documentHash: String) : CheckException(1, "HASH_NOT_PRESENT")
    class HashInconsistent(
        val documentHash: String,
        val proofDocumentHash: String
    ) : CheckException(2, "HASH_INCONSISTENT")

    class IncorrectRootHash(
        val documentHash: String,
        val proofDocumentHash: String
    ) : CheckException(3, "INCORRECT_ROOT_HASH")

    class UnknownRootHash(val rootHash: String) : CheckException(4, "UNKNOWN_ROOT_HASH")
    class DocumentKnownUnknownRootHash(
        val signer: String?,
        val publicKey: String?,
        val date: OffsetDateTime
    ) : CheckException(5, "DOCUMENT_KNOWN_UNKNOWN_ROOT_HASH")

    class IncorrectProofFile() : CheckException(6, "INCORRECT_PROOF_FILE")
    class IncorrectTransaction() : CheckException(7, "INCORRECT_TRANSACTION")
    class TransactionNotDeepEnough(
        val currentDepth: Long,
        val expectedDepth: Long
    ) : CheckException(8, "TRANSACTION_NOT_DEEP_ENOUGH")

    class IncoherentData() : CheckException(9, "INCOHERENT_DATA")
    class NoTransaction(
        val rootHash: String
    ) : CheckException(10, "NO_TRANSACTION")
    class IncoherentOriginPublicKey(
        val originPublicKey: String,
        val proofOriginPublicKey: String
    ) : CheckException(12, "INCORRECT_ORIGIN_PUBLIC_KEY")
}
