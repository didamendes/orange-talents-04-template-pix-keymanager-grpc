package br.com.zup.edu.keymanager.consulta

import br.com.zup.edu.ConsultaChaveRequest
import br.com.zup.edu.ConsultaChaveResponse
import br.com.zup.edu.KeyManagerConsultaGrpcServiceGrpc
import br.com.zup.edu.client.bc.PixKeysClient
import br.com.zup.edu.config.handler.ErrorHandler
import br.com.zup.edu.keymanager.ChavePixRepository
import br.com.zup.edu.keymanager.novo.toModel
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Validated
@ErrorHandler
@Singleton
class ConsultaKeyManagerEndpoint(@Inject private val repository: ChavePixRepository,
                                 @Inject private val bcClient: PixKeysClient,
                                 @Inject private val validator: Validator)
    : KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceImplBase() {

    override fun consultar(request: ConsultaChaveRequest, responseObserver: StreamObserver<ConsultaChaveResponse>) {

        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(repository, bcClient)

        responseObserver.onNext(ConsultaChavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()
    }

}