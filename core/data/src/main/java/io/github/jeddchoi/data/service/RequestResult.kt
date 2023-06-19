package io.github.jeddchoi.data.service

data class RequestResult(
    val code: Int = 0,
    val message: String = "",
) {
    companion object {
        fun fromResultCode(resultCode: ResultCode): RequestResult {
            return RequestResult(code = resultCode.value, message = resultCode.name)
        }
    }
}