package br.com.zup.edu.keymanager.novo

import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.client.bc.response.CreatePixKeyResponse
import br.com.zup.edu.client.itau.ClienteControllerClient
import br.com.zup.edu.config.handler.exceptions.ChavePixExistenteException
import br.com.zup.edu.keymanager.ChavePix
import br.com.zup.edu.keymanager.ChavePixRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                        @Inject val client: ClienteControllerClient,
                          @Inject val bcClient: PixKeysClient
) {

    @Transactional
    fun registrar(@Valid novaChave: NovaChavePix): ChavePix {

        if (repository.existsByValorChave(novaChave.valorChave!!)) {
            throw ChavePixExistenteException("Chave ja existe na base de dados")
        }

        val response = client.buscarContaPorTipo(novaChave.identificador!!, novaChave.tipoConta!!.name)

        if (response == null) {
            throw IllegalStateException("Cliente nao encontrado no Itau")
        }

        val conta = response.toModel()

        val chave = novaChave.toModel(conta)
        repository.save(chave)

        val createPixKeyResponse = chave.toCreatePixKeyResponse()
        val httpCriacaoPix = bcClient.criacaoPix(createPixKeyResponse)

        if (httpCriacaoPix.status != HttpStatus.CREATED) {
            throw IllegalStateException("PIX nao cadastrado")
        }

        chave.isChave(httpCriacaoPix.body())

        return chave
    }

}
