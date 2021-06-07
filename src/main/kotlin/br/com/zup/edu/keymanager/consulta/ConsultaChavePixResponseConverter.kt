package br.com.zup.edu.keymanager.consulta

import br.com.zup.edu.ConsultaChaveResponse
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta

class ConsultaChavePixResponseConverter {

    fun convert(chaveInfo: ChavePixInfo): ConsultaChaveResponse {
        return ConsultaChaveResponse.newBuilder()
            .setIdentificador(chaveInfo.identificador.toString() ?: "") // Protobuf usa "" como default value para String
            .setIdPix(chaveInfo.idPix?.toString() ?: "") // Protobuf usa "" como default value para String
            .setChave(ConsultaChaveResponse.ChavePix.newBuilder()
                .setTipo(TipoChave.valueOf(chaveInfo.tipo.name))
                .setChave(chaveInfo.chave)
                .setConta(ConsultaChaveResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(TipoConta.valueOf(chaveInfo.tipoConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setNomeDoTitular(chaveInfo.conta.nomeDoTitular)
                    .setCpfDoTitular(chaveInfo.conta.cpfDoTitular)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setNumeroDaConta(chaveInfo.conta.numero)
                    .build())
                .build())
            .build()
    }

}
