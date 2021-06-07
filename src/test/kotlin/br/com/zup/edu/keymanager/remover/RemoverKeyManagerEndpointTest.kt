package br.com.zup.edu.keymanager.remover

import br.com.zup.edu.KeyManagerRemoverGrpcServiceGrpc
import br.com.zup.edu.NovaChaveRequest
import br.com.zup.edu.RemoverChaveRequest
import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.client.bc.request.DeletePixKeyRequest
import br.com.zup.edu.client.bc.response.DeletePixKeyResponse
import br.com.zup.edu.keymanager.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoverKeyManagerEndpointTest(
    val repository: ChavePixRepository,
    val client: KeyManagerRemoverGrpcServiceGrpc.KeyManagerRemoverGrpcServiceBlockingStub
) {

    @Inject
    lateinit var bcClient: PixKeysClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    internal fun setUp() {
        val conta = ContaAssociada("Teste", "Teste", "1020", "Diogo Mendes", "03003013010", "0001", "1020")

        CHAVE_EXISTENTE = repository.save(
            ChavePix(
                UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
                TipoChave.CPF,
                "97150746420",
                TipoConta.CONTA_CORRENTE,
                conta
            )
        )
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover chave`() {
        Mockito.`when`(bcClient.deletarPix(novaChaveRequestMock().valorChave, deletePixKeyRequestMock())).thenReturn(
            HttpResponse.ok(deletePixKeyResponseMock())
        )

        val remover = client.remover(
            RemoverChaveRequest.newBuilder().setIdPix(CHAVE_EXISTENTE.id!!)
                .setIdentificador(CHAVE_EXISTENTE.identificador.toString()).build()
        )

        with(remover) {
            assertNotNull(remover.idPix)
        }
    }

    @Test
    internal fun `nao deve remover chave pois pix nao encontrado`() {

        Mockito.`when`(bcClient.deletarPix(novaChaveRequestMock().valorChave, deletePixKeyRequestMock()))
            .thenReturn(HttpResponse.unprocessableEntity())

        val error = assertThrows<StatusRuntimeException> {
            client.remover(RemoverChaveRequest.newBuilder().setIdPix(CHAVE_EXISTENTE.id!!)
                .setIdentificador(CHAVE_EXISTENTE.identificador.toString()).build())
        }

        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
        assertEquals("Falha ao tentar remover PIX no BC", error.status.description)
    }

    @Test
    internal fun `nao deve remover chave pois a chave nao foi encontrado`() {
        val error = assertThrows<StatusRuntimeException> {
            client.remover(RemoverChaveRequest.newBuilder().setIdPix(CHAVE_EXISTENTE.id!!)
                .setIdentificador("c56dfef4-7901-44fb-84e2-a2cefb157895").build())
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave nao encontrada", error.status.description)
        repository.deleteAll()
    }

    fun novaChaveRequestMock(): NovaChaveRequest {
        return NovaChaveRequest.newBuilder().setIdentificador("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(br.com.zup.edu.TipoChave.CPF)
            .setValorChave("97150746420")
            .setTipoConta(br.com.zup.edu.TipoConta.CONTA_CORRENTE)
            .build()
    }

    fun deletePixKeyRequestMock(): DeletePixKeyRequest {
        return DeletePixKeyRequest(key = "97150746420", participant = "1020")
    }

    fun deletePixKeyResponseMock(): DeletePixKeyResponse {
        return DeletePixKeyResponse(key = "97150746420", participant = "1020", deletedAt = "2021-06-01")
    }

    @Factory
    class Clients {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoverGrpcServiceGrpc.KeyManagerRemoverGrpcServiceBlockingStub? {
            return KeyManagerRemoverGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

    @MockBean(PixKeysClient::class)
    fun pixClienteMock(): PixKeysClient {
        return Mockito.mock(PixKeysClient::class.java)
    }

}