package com.sword.signature.api.sign

enum class SignMimeTypes(val value: String) {
    ALGORITHM_MIME_TYPE("message/x.sign.algorithm"),
    FLOW_NAME_MIME_TYPE("message/x.sign.flowName"),
    CALLBACK_URL_MIME_TYPE("message/x.sign.callBack")
}
