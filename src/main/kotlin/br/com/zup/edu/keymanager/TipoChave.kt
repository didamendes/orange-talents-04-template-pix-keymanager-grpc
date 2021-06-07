package br.com.zup.edu.keymanager

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {

    CPF {
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) {
                return false
            }
            if (!valorChave.matches("[0-9]+".toRegex())) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(valorChave, null)
            }
        }
    },
    CELULAR {
        override fun valida(valorChave: String?): Boolean {
            if(valorChave.isNullOrBlank()) {
                return false
            }
            return valorChave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun valida(valorChave: String?): Boolean {
            if(valorChave.isNullOrBlank()) {
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(valorChave, null)
            }
        }
    },
    CHAVE {
        override fun valida(valorChave: String?): Boolean = valorChave.isNullOrBlank()
    };

    abstract fun valida(valorChave: String?): Boolean

}