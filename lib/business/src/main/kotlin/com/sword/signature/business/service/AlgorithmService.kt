package com.sword.signature.business.service

import com.sword.signature.business.model.Algorithm

interface AlgorithmService {

    suspend fun getAlgorithmByName(algorithmName: String): Algorithm
}