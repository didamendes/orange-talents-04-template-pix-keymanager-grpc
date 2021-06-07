package br.com.zup.edu.keymanager.novo

import br.com.zup.edu.KeyManagerNovoGrpcServiceGrpc
import br.com.zup.edu.NovaChaveRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.client.bc.enum.AccountType
import br.com.zup.edu.client.bc.enum.KeyType
import br.com.zup.edu.client.bc.enum.Type
import br.com.zup.edu.client.bc.request.BankAccountRequest
import br.com.zup.edu.client.bc.request.CreatePixKeyRequest
import br.com.zup.edu.client.bc.request.OwnerRequest
import br.com.zup.edu.client.bc.response.BankAccountResponse
import br.com.zup.edu.client.bc.response.CreatePixKeyResponse
import br.com.zup.edu.client.bc.response.OwnerResponse
import br.com.zup.edu.client.itau.ClienteControllerClient
import br.com.zup.edu.client.itau.DadosDaContaResponse
import br.com.zup.edu.client.itau.InstituicaoResponse
import br.com.zup.edu.client.itau.TitularResponse
import br.com.zup.edu.keymanager.ChavePix
import br.com.zup.edu.keymanager.ChavePixRepository
import br.com.zup.edu.keymanager.ContaAssociada
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class NovoKeyManagerEndpointTest(
    val repository: ChavePixRepository,
    val client: KeyManagerNovoGrpcServiceGrpc.KeyManagerNovoGrpcServiceBlockingStub
) {

    @Inject
    lateinit var bcClient: PixKeysClient

    @Inject
    lateinit var clienteClient: ClienteControllerClient

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar uma nova chave`() {
        val novaChaveRequest = novaChaveRequestMock()
        val createPixKeyRequestMock = createPixKeyRequestMock()
        val createPixKeyResponseMock = createPixKeyResponseMock()

        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE", InstituicaoResponse("Itau", "1020"), "0001",
            "1020", TitularResponse("123", "Diogo", "030.030.030-30")
        )

        Mockito.`when`(
            clienteClient.buscarContaPorTipo(
                novaChaveRequest.identificador,
                novaChaveRequest.tipoConta.name
            )
        ).thenReturn(dadosDaContaResponse)

        Mockito.`when`(bcClient.criacaoPix(createPixKeyRequestMock))
            .thenReturn(HttpResponse.created(createPixKeyResponseMock))

        val response = client.cadastrar(novaChaveRequest)

        with(response) {
            assertNotNull(idPix)
            assertTrue(repository.existsById(idPix))
        }

    }

    @Test
    internal fun `nao deve cadastrar uma nova chave pois chave ja cadastrada`() {
        val novaChaveRequest = novaChaveRequestMock()
        val createPixKeyRequestMock = createPixKeyRequestMock()

        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE", InstituicaoResponse("Itau", "1020"), "0001",
            "1020", TitularResponse("123", "Diogo", "030.030.030-30")
        )

        Mockito.`when`(
            clienteClient.buscarContaPorTipo(
                novaChaveRequest.identificador,
                novaChaveRequest.tipoConta.name
            )
        ).thenReturn(dadosDaContaResponse)

        Mockito.`when`(bcClient.criacaoPix(createPixKeyRequestMock)).thenReturn(HttpResponse.badRequest())

        val error = assertThrows<StatusRuntimeException> {
            client.cadastrar(novaChaveRequest)
        }

        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
    }

    @Test
    internal fun `nao deve cadastrar nova chave pois valor chave existente`() {
        val novaChaveRequest = novaChaveRequestMock()
        val conta = ContaAssociada("Teste", "Teste", "97150746420", "Diogo Mendes", "03003013010", "2020", "123456")
        val chavePix = ChavePix(
            UUID.randomUUID(),
            br.com.zup.edu.keymanager.TipoChave.CPF,
            novaChaveRequest.valorChave,
            br.com.zup.edu.keymanager.TipoConta.CONTA_CORRENTE,
            conta
        )

        repository.save(chavePix)

        val error = assertThrows<StatusRuntimeException> {
            client.cadastrar(novaChaveRequest)
        }

        assertEquals(Status.ALREADY_EXISTS.code, error.status.code)
        assertEquals("Chave ja existe na base de dados", error.status.description)
    }

    @Test
    internal fun `nao deve cadastrar nova chave pois cliente nao encontrado`() {
        val novaChaveRequest = novaChaveRequestMock()

        Mockito.`when`(
            clienteClient.buscarContaPorTipo(
                novaChaveRequest.identificador,
                novaChaveRequest.tipoConta.name
            )
        ).thenReturn(null)

        val error = assertThrows<StatusRuntimeException> {
            client.cadastrar(novaChaveRequest)
        }

        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
        assertEquals("Cliente nao encontrado no Itau", error.status.description)
    }

    fun novaChaveRequestMock(): NovaChaveRequest {
        return NovaChaveRequest.newBuilder().setIdentificador("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("97150746420")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
    }

    fun createPixKeyRequestMock(): CreatePixKeyRequest {
        val bankAccountRequest = BankAccountRequest(
            participant = "1020",
            branch = "0001",
            accountNumber = "1020",
            accountType = AccountType.CACC
        )

        val ownerRequest = OwnerRequest(
            type = Type.NATURAL_PERSON,
            name = "Diogo",
            taxIdNumber = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        )

        return CreatePixKeyRequest(
            keyType = KeyType.CPF,
            key = "97150746420",
            bankAccount = bankAccountRequest,
            owner = ownerRequest
        )
    }

    fun createPixKeyResponseMock(): CreatePixKeyResponse {
        val bankAccountRequest = BankAccountResponse(
            participant = "1020",
            branch = "0001",
            accountNumber = "1020",
            accountType = BankAccountResponse.AccountType.CACC
        )

        val ownerRequest = OwnerResponse(
            type = Type.NATURAL_PERSON,
            name = "Diogo",
            taxIdNumber = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        )

        return CreatePixKeyResponse(
            keyType = KeyType.CPF,
            key = "97150746420",
            bankAccount = bankAccountRequest,
            owner = ownerRequest,
            createdAt = "2021-06-07"
        )
    }

    @Factory
    class Clients {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerNovoGrpcServiceGrpc.KeyManagerNovoGrpcServiceBlockingStub {
            return KeyManagerNovoGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ClienteControllerClient::class)
    fun clienteMock(): ClienteControllerClient {
        return Mockito.mock(ClienteControllerClient::class.java)
    }

    @MockBean(PixKeysClient::class)
    fun pixClienteMock(): PixKeysClient {
        return Mockito.mock(PixKeysClient::class.java)
    }

}