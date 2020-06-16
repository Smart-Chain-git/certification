package com.sword.signature.rest.resthandler

import com.sword.signature.api.proof.Proof
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.service.SignService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path:/api}")
class ProofHandler(
    private val signService: SignService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/proof/{fileId}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getProof(
        @AuthenticationPrincipal requester: CustomUserDetails,
        @PathVariable("fileId") fileId: String
    ): Proof {
        val proof: com.sword.signature.business.model.Proof =
            signService.getFileProof(requester = requester.account, fileId = fileId)
                ?: throw EntityNotFoundException("file", fileId)
        return proof.toWeb()
    }
}