package br.com.zup.edu.client.bc.request

data class DeletePixKeyRequest(
    val key: String,
    val participant: String
)