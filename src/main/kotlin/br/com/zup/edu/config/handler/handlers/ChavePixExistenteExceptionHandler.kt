package br.com.zup.edu.config.handler.handlers

import br.com.zup.edu.config.handler.ExceptionHandler
import br.com.zup.edu.config.handler.exceptions.ChavePixExistenteException
import br.com.zup.edu.config.handler.exceptions.ChavePixNaoExistenteException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler: ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(Status.ALREADY_EXISTS
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }

}