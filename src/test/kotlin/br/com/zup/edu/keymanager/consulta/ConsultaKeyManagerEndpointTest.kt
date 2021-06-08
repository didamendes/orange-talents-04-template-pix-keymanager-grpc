package br.com.zup.edu.keymanager.consulta

import br.com.zup.edu.ConsultaChaveRequest
import br.com.zup.edu.KeyManagerConsultaGrpcServiceGrpc
import br.com.zup.edu.client.bc.PixKeyDetailsResponse
import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.client.bc.enum.Type
import br.com.zup.edu.client.bc.response.BankAccountResponse
import br.com.zup.edu.client.bc.response.OwnerResponse
import br.com.zup.edu.client.bc.response.PixKeyType
import br.com.zup.edu.keymanager.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ConsultaKeyManagerEndpointTest(
    val repository: ChavePixRepository,
    val client: KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub
) {

    @Inject
    lateinit var bcClient: PixKeysClient

    @BeforeEach
    internal fun setUp() {
        repository.save(chave(TipoChave.CPF, "030.030.030-30", identificador = UUID.randomUUID()))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve pesquisar pelo PIX ID e identificador`() {
        val chaveExistente = repository.findByValorChave("030.030.030-30").get()

        val response = client.consultar(
            ConsultaChaveRequest.newBuilder()
                .setIdPix(
                    ConsultaChaveRequest.FiltroProPixId.newBuilder()
                        .setIdentificador(chaveExistente.identificador.toString())
                        .setIdPix(chaveExistente.id!!)
                        .build()
                )
                .build()
        )

        assertNotNull(response)
    }

    @Test
    internal fun `nao deve pesquisar pelo PIX ID e identificador pois PIX ID e identificador inexistente`() {

        val error = assertThrows<StatusRuntimeException> {
            client.consultar(
                ConsultaChaveRequest.newBuilder()
                    .setIdPix(
                        ConsultaChaveRequest.FiltroProPixId.newBuilder()
                            .setIdentificador(UUID.randomUUID().toString())
                            .setIdPix(1)
                            .build()
                    )
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave PIX nao encontrada", error.status.description)
    }

    @Test
    internal fun `deve pesquisar pela chave`() {
        val chaveExistente = repository.findByValorChave("030.030.030-30").get()

        val response = client.consultar(
            ConsultaChaveRequest.newBuilder()
                .setChave(chaveExistente.valorChave).build()
        )

        assertNotNull(response)
    }

    @Test
    internal fun `deve pesquisar pela chave no BC`() {
        val pixKeyDetailsResponse = pixKeyDetailsResponse()

        Mockito.`when`(bcClient.findByKey("user.from.another.bank@santander.com.br"))
            .thenReturn(HttpResponse.ok(pixKeyDetailsResponse))

        val response = client.consultar(
            ConsultaChaveRequest.newBuilder()
                .setChave("user.from.another.bank@santander.com.br").build()
        )

        assertNotNull(response)
    }

    @Test
    internal fun `nao deve pesquisar pela chave no BC pois chave inexistente`() {
        Mockito.`when`(bcClient.findByKey("notfound.from.another.bank@santander.com.br"))
            .thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            client.consultar(
                ConsultaChaveRequest.newBuilder().setChave("notfound.from.another.bank@santander.com.br").build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave PIX nao encontrada", error.status.description)
    }

    @Test
    fun `nao deve consultar chave quando filtro invalido`() {
        val error = assertThrows<StatusRuntimeException> {
            client.consultar(ConsultaChaveRequest.newBuilder().build())
        }

            assertEquals(Status.UNKNOWN.code, error.status.code)
            assertEquals("Chave Pix inválida ou não informada", error.status.description)
    }

    private fun chave(
        tipoChave: TipoChave,
        valorChave: String = UUID.randomUUID().toString(),
        identificador: UUID = UUID.randomUUID()
    ): ChavePix {
        return ChavePix(
            identificador = identificador,
            tipoChave = tipoChave,
            valorChave = valorChave,
            tipoConta = TipoConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                tipo = "CACC",
                instituicao = "Itau",
                ispb = "1020",
                nomeDoTitular = "Diogo",
                cpfDoTitular = "030.030.030-30",
                agencia = "0001",
                numero = "1020"
            )
        )
    }

    private fun pixKeyDetailsResponse(): PixKeyDetailsResponse {
        return PixKeyDetailsResponse(
            keyType = PixKeyType.EMAIL,
            key = "user.from.another.bank@santander.com.br",
            bankAccount = BankAccountResponse(
                participant = "SANTANDER",
                branch = "1234",
                accountNumber = "654321",
                accountType = BankAccountResponse.AccountType.CACC
            ),
            owner = OwnerResponse(
                type = Type.NATURAL_PERSON,
                name = "John Smith",
                taxIdNumber = "45826781068"
            ),
            createdAt = LocalDateTime.now()
        )
    }

    // extrai os detalhes de dentro do erro
    fun violations(e: StatusRuntimeException): String? {
        val details = StatusProto.fromThrowable(e)?.allFields?.values
        return details?.toString()
    }

    @Factory
    class Clients {

        @Replaces
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub {
            return KeyManagerConsultaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(PixKeysClient::class)
    fun pixClienteMock(): PixKeysClient {
        return Mockito.mock(PixKeysClient::class.java)
    }
}