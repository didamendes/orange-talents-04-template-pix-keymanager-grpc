package br.com.zup.edu.keymanager.novo

import br.com.zup.edu.keymanager.ChavePix
import br.com.zup.edu.keymanager.ContaAssociada
import br.com.zup.edu.keymanager.TipoChave
import br.com.zup.edu.keymanager.TipoConta
import br.com.zup.edu.validacao.ValidPixKey
import br.com.zup.edu.validacao.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(
    @field:ValidUUID
    @field:NotBlank
    val identificador: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:Size(max = 77)
    val valorChave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            identificador = UUID.fromString(identificador),
            tipoChave = TipoChave.valueOf(tipoChave!!.name),
            valorChave = if (tipoChave == TipoChave.CHAVE) UUID.randomUUID().toString() else valorChave!!,
            tipoConta = TipoConta.valueOf(tipoConta!!.name),
            conta = conta
        )
    }

}
