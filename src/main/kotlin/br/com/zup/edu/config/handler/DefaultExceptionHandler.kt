package br.com.zup.edu.config.handler

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class DefaultExceptionHandler: ExceptionHandler<Exception> {

    override fun handle(e: Exception): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.UNKNOWN
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is NullPointerException
    }

}