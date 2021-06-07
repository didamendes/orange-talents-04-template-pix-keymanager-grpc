package br.com.zup.edu.client.itau

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091/api/v1/clientes")
interface ClienteControllerClient {

    @Get(uri = "/{clienteId}/contas{?tipo}")
    fun buscarContaPorTipo(@PathVariable("clienteId") identificador: String, @QueryValue tipo: String): DadosDaContaResponse

}