package br.com.zup.edu.keymanager

import br.com.zup.edu.client.bc.request.CreatePixKeyRequest
import javax.persistence.Embeddable

@Embeddable
data class ContaAssociada(
    val tipo: String,
    val instituicao: String,
    val ispb: String,
    val nomeDoTitular: String,
    val cpfDoTitular: String,
    val agencia: String,
    val numero: String
)