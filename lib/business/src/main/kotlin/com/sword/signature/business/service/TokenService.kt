package com.sword.signature.business.service

import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.model.repository.TokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


interface TokenService {

    suspend fun getToken(token: String): Token?

    suspend fun checkAndGetToken(token: String): Token
}