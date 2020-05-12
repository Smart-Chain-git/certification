package com.sword.signature.business.service

import com.sword.signature.business.model.Algorithm
import kotlinx.coroutines.flow.Flow

interface AlgorithmService {

    suspend fun getAlgorithmByName(algorithmName: String): Algorithm

    fun findAll(): Flow<Algorithm>

}