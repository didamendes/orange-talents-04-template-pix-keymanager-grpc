package br.com.zup.edu.client.bc.request

import br.com.zup.edu.client.bc.enum.AccountType

data class BankAccountRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)