package br.com.zup.edu.client.bc

import br.com.zup.edu.client.bc.enum.KeyType
import br.com.zup.edu.client.bc.response.BankAccountResponse
import br.com.zup.edu.client.bc.response.OwnerResponse
import br.com.zup.edu.client.bc.response.PixKeyType
import br.com.zup.edu.keymanager.ContaAssociada
import br.com.zup.edu.keymanager.TipoConta
import br.com.zup.edu.keymanager.consulta.ChavePixInfo
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoConta = when (this.bankAccount.accountType) {
                BankAccountResponse.AccountType.CACC -> TipoConta.CONTA_CORRENTE
                BankAccountResponse.AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                tipo = keyType.domainType!!.name,
                instituicao = bankAccount.participant,
                ispb = bankAccount.participant,
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
            )
        )
    }
}
