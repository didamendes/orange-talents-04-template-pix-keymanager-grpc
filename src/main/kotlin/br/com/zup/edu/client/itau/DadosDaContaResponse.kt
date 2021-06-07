package br.com.zup.edu.client.itau

import br.com.zup.edu.keymanager.ContaAssociada

class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): ContaAssociada {
        return ContaAssociada(
            tipo =  if (tipo.equals("CONTA_CORRENTE")) "CACC" else "SVGS",
            instituicao = instituicao.nome,
            ispb = instituicao.ispb,
            nomeDoTitular = titular.nome,
            cpfDoTitular = titular.cpf,
            agencia = agencia,
            numero = numero
        )
    }
}