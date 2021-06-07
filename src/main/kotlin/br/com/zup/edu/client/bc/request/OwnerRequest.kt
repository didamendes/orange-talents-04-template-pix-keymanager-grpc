package br.com.zup.edu.client.bc.request

import br.com.zup.edu.client.bc.enum.Type

data class OwnerRequest(
    val type: Type,
    val name: String,
    val taxIdNumber: String
)