package com.sword.signature.business.service

import com.sword.signature.business.exception.AlgorithmNotFoundException
import com.sword.signature.business.model.Algorithm
import kotlinx.coroutines.flow.Flow

interface AlgorithmService {

    /**
     * Retrieve the algorithm by its name.
     * @param algorithmName Name of the algorithm.
     * @throws AlgorithmNotFoundException if the name does not match with an existing algorithm.
     * @return The algorithm.
     */
    @Throws(AlgorithmNotFoundException::class)
    suspend fun getAlgorithmByName(algorithmName: String): Algorithm

    /**
     * Retrieve all existing algorithms.
     * @return All existing algorithms.
     */
    fun findAll(): Flow<Algorithm>
}
