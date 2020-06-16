package com.sword.signature.rest.resthandler

import com.sword.signature.api.algorithm.Algorithm
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path:/api}")
class AlgorithmHandler(
    val algorithmService: AlgorithmService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/algorithms"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun algorithms(
        @AuthenticationPrincipal user: CustomUserDetails
    ): Flow<Algorithm> {
        val algorithms = algorithmService.findAll()
        return algorithms.map { it.toWeb() }
    }
}


