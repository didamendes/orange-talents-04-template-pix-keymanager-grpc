package br.com.zup.edu.client.bc.response

import br.com.zup.edu.client.bc.enum.Type

data class OwnerResponse(
    val type: Type,
    val name: String,
    val taxIdNumber: String
)