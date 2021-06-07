package br.com.zup.edu.keymanager.novo

import br.com.zup.edu.KeyManagerNovoGrpcServiceGrpc
import br.com.zup.edu.NovaChaveRequest
import br.com.zup.edu.NovaChaveResponse
import br.com.zup.edu.config.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class NovoKeyManagerEndpoint(@Inject private val service: NovaChavePixService)
    : KeyManagerNovoGrpcServiceGrpc.KeyManagerNovoGrpcServiceImplBase() {

    override fun cadastrar(request: NovaChaveRequest, responseObserver: StreamObserver<NovaChaveResponse>) {
        val chave = request.toModel()
        val chaveCriada = service.registrar(chave)

        responseObserver.onNext(NovaChaveResponse.newBuilder().setIdPix(chaveCriada.id!!).build())
        responseObserver.onCompleted()
    }

}