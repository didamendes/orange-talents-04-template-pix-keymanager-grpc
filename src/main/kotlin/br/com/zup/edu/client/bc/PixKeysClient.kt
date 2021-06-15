package br.com.zup.edu.client.bc

import br.com.zup.edu.client.bc.request.CreatePixKeyRequest
import br.com.zup.edu.client.bc.request.DeletePixKeyRequest
import br.com.zup.edu.client.bc.response.CreatePixKeyResponse
import br.com.zup.edu.client.bc.response.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}/api/v1/pix")
interface PixKeysClient {

    @Post(uri = "/keys", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun criacaoPix(@Body createPixKeyRequest: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(uri = "/keys/{key}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deletarPix(@PathVariable("key") key: String, @Body deletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get("/keys/{key}",
        consumes = [MediaType.APPLICATION_XML])
    fun findByKey(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>

}