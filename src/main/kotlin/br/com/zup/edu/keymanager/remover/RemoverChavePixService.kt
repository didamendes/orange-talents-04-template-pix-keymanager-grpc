package br.com.zup.edu.keymanager.remover

import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.client.bc.request.DeletePixKeyRequest
import br.com.zup.edu.config.handler.exceptions.ChavePixNaoExistenteException
import br.com.zup.edu.keymanager.ChavePixRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Singleton
class RemoverChavePixService(val repository: ChavePixRepository,
                             val bcClient: PixKeysClient) {

    @Transactional
    fun remover(@NotNull idPix: Long,
                @NotBlank identificador: String?) {

        val uuidIdentificador = UUID.fromString(identificador)

        val chave = repository.findByIdAndIdentificador(idPix, uuidIdentificador)
            .orElseThrow { throw ChavePixNaoExistenteException("Chave nao encontrada") }

        val deletePixKeyRequest = DeletePixKeyRequest(chave.valorChave, chave.conta.ispb)

        val httpDeletarPix = bcClient.deletarPix(chave.valorChave, deletePixKeyRequest)

        if (httpDeletarPix.status != HttpStatus.OK) {
            throw IllegalStateException("Falha ao tentar remover PIX no BC")
        }

        repository.deleteById(idPix)
    }

}
