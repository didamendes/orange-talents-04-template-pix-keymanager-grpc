package br.com.zup.edu.config.handler.exceptions

import java.lang.RuntimeException

class ChavePixNaoExistenteException(message: String?): RuntimeException(message)