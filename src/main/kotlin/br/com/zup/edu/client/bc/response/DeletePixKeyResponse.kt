package br.com.zup.edu.client.bc.response

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: String
)