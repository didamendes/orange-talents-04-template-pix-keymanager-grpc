package br.com.zup.edu.keymanager.listar

import br.com.zup.edu.KeyManagerListarGrpcServiceGrpc
import br.com.zup.edu.ListaChaveRequest
import br.com.zup.edu.keymanager.*
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import io.micronaut.grpc.server.GrpcServerChannel as GrpcServerChannel1

@MicronautTest(transactional = false)
internal class ListaKeyManagerEndpointTest(
    @Inject private val repository: ChavePixRepository,
    @Inject private val client: KeyManagerListarGrpcServiceGrpc.KeyManagerListarGrpcServiceBlockingStub
) {

    companion object {
        val identificador = UUID.randomUUID()
    }

    @BeforeEach
    internal fun setUp() {
        repository.save(chave(TipoChave.EMAIL, "diogo.souza@zup.com.br", identificador))
        repository.save(chave(TipoChave.CHAVE, "teste", identificador))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve buscar todos as chave pix do cliente informado`() {

        val listar = client.listar(ListaChaveRequest.newBuilder().setIndentificador(identificador.toString()).build())

        Assertions.assertEquals(2, listar.chavesCount)
    }

    @Test
    internal fun `deve buscar chave pix do cliente informado porem cliente nao possui chave cadastrada`() {
        val listar =
            client.listar(ListaChaveRequest.newBuilder().setIndentificador(UUID.randomUUID().toString()).build())

        Assertions.assertEquals(0, listar.chavesCount)
    }

    @Test
    internal fun `nao deve buscar chave pix pois o cliente nao foi informado`() {
        val error = assertThrows<StatusRuntimeException> {
            client.listar(ListaChaveRequest.newBuilder().build())
        }
        Assertions.assertEquals("Identificador nao pode ser nulo ou branco", error.status.description)
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

    // extrai os detalhes de dentro do erro
    fun violations(e: StatusRuntimeException): String? {
        val details = StatusProto.fromThrowable(e)?.allFields?.values
        return details?.toString()
    }

    @Factory
    class Clients {

        @Replaces
        @Singleton
        fun blockingSubs(@GrpcChannel(GrpcServerChannel1.NAME) channel: ManagedChannel): KeyManagerListarGrpcServiceGrpc.KeyManagerListarGrpcServiceBlockingStub? {
            return KeyManagerListarGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}