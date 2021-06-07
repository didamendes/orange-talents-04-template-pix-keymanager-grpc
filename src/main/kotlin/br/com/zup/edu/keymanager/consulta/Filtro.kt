package br.com.zup.edu.keymanager.consulta

import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.config.handler.exceptions.ChavePixNaoExistenteException
import br.com.zup.edu.keymanager.ChavePixRepository
import br.com.zup.edu.validacao.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    val logger = LoggerFactory.getLogger(this::class.java)

    abstract fun filtra(repository: ChavePixRepository, bcClient: PixKeysClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val identificador: String,
        @field:NotNull val idPix: Long
    ): Filtro() {

        fun uuidIdentificador() = UUID.fromString(identificador)

        override fun filtra(repository: ChavePixRepository, bcClient: PixKeysClient): ChavePixInfo {
            logger.info("Consultando chave Pix '${idPix}' no sistema interno")

            return repository.findById(idPix)
                .filter { it.pertenceAo(uuidIdentificador()) }
                .map(ChavePixInfo::of)
                .orElseThrow{ ChavePixNaoExistenteException("Chave PIX nao encontrada") }
        }

    }

    @Introspected
    data class PorChave(@field:NotBlank @field:Size(max = 77) val chave: String): Filtro() {
        override fun filtra(repository: ChavePixRepository, bcClient: PixKeysClient): ChavePixInfo {
            logger.info("Consultando chave Pix '${chave}' no sistema interno")

            return repository.findByValorChave(chave)
                .map(ChavePixInfo::of)
                .orElseGet {
                    val response = bcClient.findByKey(chave)

                    when (response.status) {
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixNaoExistenteException("Chave PIX nao encontrada")
                    }
                }
        }
    }

    @Introspected
    class Invalido(): Filtro() {

        override fun filtra(repository: ChavePixRepository, bcClient: PixKeysClient): ChavePixInfo {
            throw IllegalArgumentException("Chave Pix inválida ou não informada")
        }

    }

}