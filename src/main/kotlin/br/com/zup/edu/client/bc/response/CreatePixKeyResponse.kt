package br.com.zup.edu.client.bc.response

import br.com.zup.edu.client.bc.enum.KeyType

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
)