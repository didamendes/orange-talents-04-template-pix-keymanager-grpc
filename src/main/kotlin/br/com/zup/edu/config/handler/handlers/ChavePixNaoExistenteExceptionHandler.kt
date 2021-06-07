package br.com.zup.edu.config.handler.handlers

import br.com.zup.edu.config.handler.ExceptionHandler
import br.com.zup.edu.config.handler.exceptions.ChavePixNaoExistenteException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixNaoExistenteExceptionHandler: ExceptionHandler<ChavePixNaoExistenteException> {

    override fun handle(e: ChavePixNaoExistenteException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(Status.NOT_FOUND
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixNaoExistenteException
    }

}