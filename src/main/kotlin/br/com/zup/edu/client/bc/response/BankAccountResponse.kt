package br.com.zup.edu.client.bc.response

import br.com.zup.edu.client.bc.enum.AccountType
import br.com.zup.edu.keymanager.TipoChave
import br.com.zup.edu.keymanager.TipoConta

data class BankAccountResponse(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType() {
        CACC,
        SVGS;

        companion object {
            fun by(domainType: TipoConta): AccountType {
                return when (domainType) {
                    TipoConta.CONTA_CORRENTE -> CACC
                    TipoConta.CONTA_POUPANCA -> SVGS
                }
            }
        }
    }
}

enum class PixKeyType(val domainType: TipoChave?) {
    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.CHAVE);

    companion object {
        private val mapping = PixKeyType.values().associateBy(PixKeyType::domainType)

        fun by(domainType: TipoChave): PixKeyType {
            return mapping[domainType] ?: throw IllegalArgumentException("PixKeyType invalid")
        }
    }
}