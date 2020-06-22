package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.FileFilter
import com.sword.signature.business.model.Proof
import com.sword.signature.business.model.TreeElement
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FileService {

    /**
     * Retrieve all files matching the filter.
     * @param requester The account requesting the files.
     * @param filter Filter for file search.
     * @param pageable Pageable for memory-optimized file retrieval.
     * @return Files matching the filter.
     */
    suspend fun getFiles(
        requester: Account,
        filter: FileFilter? = null,
        pageable: Pageable = Pageable.unpaged()
    ): Flux<TreeElement.LeafTreeElement>

    /**
     * Compute the proof for a given file.
     * @param requester The account requesting the proof.
     * @param fileId Id of the file to compute the proof of.
     * @return Proof of the file.
     */
    suspend fun getFileProof(requester: Account, fileId: String): Mono<Proof>
}