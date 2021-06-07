package br.com.zup.edu.client.bc.request

import br.com.zup.edu.client.bc.enum.KeyType

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest
)