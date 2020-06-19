package com.sword.signature.business.model

sealed class URLNode(
    val url: String,
    val type: String,
    val comment: String? = null
) {

    class WebSignerURLNode(url: String) : URLNode(
        url = url,
        type = "WEB_SIGNER",
        comment = "URL to check the signer."
    )

    class ApiStorageURLNode(
        url: String,
        bigMapId: String,
        rootHash: String
    ) : URLNode(
        url = url.replace(BIGMAP_ID_PLACEHOLDER, bigMapId).replace(ROOTHASH_PLACEHOLDER, rootHash),
        type = "API_TEZOS_STORAGE",
        comment = "You can check the hash's anchorage in the tezos blockchain with this URL."
    )

    class ApiTransactionURLNode(
        url: String,
        transactionHash: String
    ) : URLNode(
        url = url.replace(TRANSACTION_HASH_PLACEHOLDER, transactionHash),
        type = "API_TEZOS_TRANSACTION",
        comment = "You can check the transaction in the tezos blockchain with this URL."
    )

    class WebProviderURLNode(url: String) : URLNode(
        url = url,
        type = "WEB_PROVIDER",
        comment = "URL to check the signature."
    )

    companion object {
        val BIGMAP_ID_PLACEHOLDER = "\$bigmapId"
        val ROOTHASH_PLACEHOLDER = "\$rootHash"
        val TRANSACTION_HASH_PLACEHOLDER = "\${transactionHash}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as URLNode

        if (url != other.url) return false
        if (type != other.type) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }
}