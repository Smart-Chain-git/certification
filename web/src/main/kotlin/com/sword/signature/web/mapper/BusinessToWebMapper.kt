package com.sword.signature.web.mapper

import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.model.SignJob

fun SignJob.toWeb() = SignResponse(jobId = id,files = files?: emptyList())