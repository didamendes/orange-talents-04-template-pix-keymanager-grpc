package br.com.zup.edu.keymanager

import br.com.zup.edu.client.bc.enum.AccountType
import br.com.zup.edu.client.bc.enum.KeyType
import br.com.zup.edu.client.bc.enum.Type
import br.com.zup.edu.client.bc.request.BankAccountRequest
import br.com.zup.edu.client.bc.request.CreatePixKeyRequest
import br.com.zup.edu.client.bc.request.OwnerRequest
import br.com.zup.edu.client.bc.response.CreatePixKeyResponse
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @field:NotNull
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val identificador: UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoChave: TipoChave,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    var valorChave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoConta: TipoConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada
) {
    @Id
    @GeneratedValue
    val id: Long? = null

    fun toCreatePixKeyResponse(): CreatePixKeyRequest {
        var keyType: KeyType
        var accountType: AccountType

        when {
            TipoConta.CONTA_CORRENTE == tipoConta -> accountType = AccountType.CACC
            else -> accountType = AccountType.SVGS
        }

        when {
            TipoChave.CPF == tipoChave -> keyType = KeyType.CPF
            TipoChave.CELULAR == tipoChave -> keyType = KeyType.PHONE
            TipoChave.EMAIL == tipoChave -> keyType = KeyType.EMAIL
            else -> keyType = KeyType.RANDOM
        }

        val ownerRequest =
            OwnerRequest(type = Type.NATURAL_PERSON, name = conta.nomeDoTitular, taxIdNumber = identificador.toString())

        val bankAccountRequest = BankAccountRequest(
            participant = conta.ispb,
            branch = conta.agencia,
            accountNumber = conta.numero,
            accountType = accountType
        )
        return CreatePixKeyRequest(keyType = keyType, key = valorChave, bankAccount = bankAccountRequest, owner = ownerRequest)
    }

    fun isChave(criacaoPix: CreatePixKeyResponse) {
        if (KeyType.RANDOM.id.equals(criacaoPix.keyType.id)) {
            valorChave = criacaoPix.key
        }
    }

    fun pertenceAo(identificador: UUID?) = this.identificador == identificador
}
