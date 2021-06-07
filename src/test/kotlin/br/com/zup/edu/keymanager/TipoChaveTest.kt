package br.com.zup.edu.keymanager

import io.grpc.StatusRuntimeException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class CPF {
        @Test
        internal fun `deve ser valido o CPF`() {
            with(TipoChave.CPF) {
                assertTrue(valida("39433141709"))
            }
        }

        @Test
        internal fun `deve ser invalido quando CPF invalido nulo branco`() {
            with(TipoChave.CPF) {
                assertFalse(valida("39433141701"))
                assertFalse(valida("3943314asds"))
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }
    }

    @Nested
    inner class EMAIL {
        @Test
        internal fun `deve ser valido email`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("didamendes@hotmail.com"))
            }
        }

        @Test
        internal fun `nao deve ser valido email`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("didamendes"))
            }
        }
    }

    @Nested
    inner class CELULAR {
        @Test
        internal fun `deve ser valido o celular`() {
            with(TipoChave.CELULAR) {
                assertTrue(valida("+5562992320099"))
            }
        }

        @Test
        internal fun `nao deve ser valido o celular`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("62992320099"))
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }
    }

    @Nested
    inner class CHAVE {

         @Test
         fun `deve ser valido quando chave for nula`() {
             with(TipoChave.CHAVE) {
                 assertTrue(valida(null))
                 assertTrue(valida(""))
             }
         }

        @Test
        internal fun `nao deve ser valido quando a chave possuir um valor`() {
            with(TipoChave.CHAVE) {
                assertFalse(valida("valor"))
            }
        }
    }


}