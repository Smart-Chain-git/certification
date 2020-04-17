package com.sword.signature.web.mapper

import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.model.Job

fun Job.toWeb() = SignResponse(jobId = id, files = files?.map { it.fileName } ?: emptyList())