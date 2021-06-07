package br.com.zup.edu.keymanager.consulta

import br.com.zup.edu.keymanager.ChavePix
import br.com.zup.edu.keymanager.ContaAssociada
import br.com.zup.edu.keymanager.TipoChave
import br.com.zup.edu.keymanager.TipoConta
import java.util.*

data class ChavePixInfo(
    val idPix: Long? = null,
    val identificador: UUID? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: ContaAssociada
) {

    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                idPix = chave.id,
                identificador = chave.identificador,
                tipo = chave.tipoChave,
                chave = chave.valorChave,
                tipoConta = chave.tipoConta,
                conta = chave.conta
            )
        }
    }

}
