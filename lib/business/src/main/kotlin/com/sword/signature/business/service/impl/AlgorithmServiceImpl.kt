package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.model.repository.AlgorithmRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AlgorithmServiceImpl(
        private val algorithmRepository: AlgorithmRepository
) : AlgorithmService {

    override suspend fun getAlgorithmByName(algorithmName: String): Algorithm? {
        LOGGER.debug("Retrieving algorithm with name '{}'.", algorithmName)
        val algorithm = algorithmRepository.findByName(algorithmName)?.toBusiness()
        LOGGER.debug("Retrieved algorithm with name  '{}'.", algorithm)
        return algorithm
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AlgorithmServiceImpl::class.java)
    }

}