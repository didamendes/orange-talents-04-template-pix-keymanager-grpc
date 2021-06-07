package br.com.zup.edu.keymanager.remover

import br.com.zup.edu.KeyManagerRemoverGrpcServiceGrpc
import br.com.zup.edu.RemoverChaveRequest
import br.com.zup.edu.RemoverChaveResponse
import br.com.zup.edu.config.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoverKeyManagerEndpoint(@Inject private val service: RemoverChavePixService )
    : KeyManagerRemoverGrpcServiceGrpc.KeyManagerRemoverGrpcServiceImplBase() {

    override fun remover(request: RemoverChaveRequest, responseObserver: StreamObserver<RemoverChaveResponse>) {
        service.remover(request.idPix, request.identificador)

        responseObserver.onNext(RemoverChaveResponse.newBuilder().setIdPix(request.idPix).build())
        responseObserver.onCompleted()
    }

}