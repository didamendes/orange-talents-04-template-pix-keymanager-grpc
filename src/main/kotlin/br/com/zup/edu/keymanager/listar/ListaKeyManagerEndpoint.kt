package br.com.zup.edu.keymanager.listar

import br.com.zup.edu.*
import br.com.zup.edu.config.handler.ErrorHandler
import br.com.zup.edu.keymanager.ChavePixRepository
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaKeyManagerEndpoint(@Inject private val repository: ChavePixRepository) :
    KeyManagerListarGrpcServiceGrpc.KeyManagerListarGrpcServiceImplBase() {

    override fun listar(request: ListaChaveRequest, responseObserver: StreamObserver<ListaChaveResponse>) {

        if (request.indentificador.isNullOrBlank()) {
            throw IllegalArgumentException("Identificador nao pode ser nulo ou branco")
        }

        val uuidIdentificador = UUID.fromString(request.indentificador)
        val chaves = repository.findAllByIdentificador(uuidIdentificador).map {
            ListaChaveResponse.chave.newBuilder().setIdPix(it.id!!).setTipoChave(TipoChave.valueOf(it.tipoChave.name))
                .setValorChave(it.valorChave).setTipoConta(
                    TipoConta.valueOf(it.tipoConta.name)
                ).build()
        }

        responseObserver.onNext(
            ListaChaveResponse.newBuilder().setIdentificador(request.indentificador).addAllChaves(chaves).build()
        )
        responseObserver.onCompleted()
    }

}