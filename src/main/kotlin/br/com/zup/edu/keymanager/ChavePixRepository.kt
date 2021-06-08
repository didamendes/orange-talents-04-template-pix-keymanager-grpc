package br.com.zup.edu.keymanager

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, Long> {

    fun existsByValorChave(valorChave: String): Boolean

    @Query( "SELECT ch FROM ChavePix ch WHERE ch.id = :id AND ch.identificador = :identificador")
    fun findByIdAndIdentificador(id: Long, identificador: UUID): Optional<ChavePix>

    fun findByValorChave(valorChave: String): Optional<ChavePix>

    fun findAllByIdentificador(identificador: UUID): List<ChavePix>

}
