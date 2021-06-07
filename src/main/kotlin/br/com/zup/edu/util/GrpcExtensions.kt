package br.com.zup.edu.keymanager.novo

import br.com.zup.edu.ConsultaChaveRequest
import br.com.zup.edu.NovaChaveRequest
import br.com.zup.edu.ConsultaChaveRequest.FiltroCase.*
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.keymanager.consulta.Filtro
import javax.validation.ConstraintViolationException
import javax.validation.Validator


fun NovaChaveRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        identificador = identificador,
        tipoChave = if (tipoChave == TipoChave.NULLO) { null }
        else { br.com.zup.edu.keymanager.TipoChave.valueOf(tipoChave.name) },
        valorChave = valorChave,
        tipoConta = if (tipoConta == TipoConta.NULO) { null } else { br.com.zup.edu.keymanager.TipoConta.valueOf(tipoConta.name) }
    )
}

fun ConsultaChaveRequest.toModel(validator: Validator): Filtro {
    val filtro = when(filtroCase) {
        IDPIX -> idPix.let { Filtro.PorPixId(identificador = it.identificador, idPix = it.idPix) }
        CHAVE -> Filtro.PorChave(chave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations);
    }

    return filtro
}