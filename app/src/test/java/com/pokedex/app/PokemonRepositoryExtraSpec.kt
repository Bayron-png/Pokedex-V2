package com.pokedex.app

import com.pokedex.app.data.model.*
import com.pokedex.app.data.remote.PokeApi
import com.pokedex.app.repository.PokemonRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class PokemonRepositoryExtraSpec : FunSpec({
    // Ficha de ejemplo de Pikachu.
    val ficha = PokemonDto(
        25,
        "pikachu",
        4,
        60,
        SpritesDto(null),
        listOf(PokemonTypeDto(1, TypeDto("electric")))
    )

    test("el Repository elimina los espacios al buscar por número") {
        // Mock: La API falsa que responde cuando recibe el número 25.
        val api = mockk<PokeApi>()

        // Cada vez que el nombre sea 25, debe retornar la ficha de Pikachu
        coEvery {
            api.obtenerPokemon("25")
        } returns ficha

        val repo = PokemonRepository(api)

        // Buscar Pokémon escribiendo espacios alrededor del número
        val resultado = repo.buscar(" 25 ")

        // Entonces: obtiene la ficha de Pikachu
        resultado shouldBe ficha

        // Verifica que la API recibió el número sin espacios.
        coVerify(exactly = 1) { api.obtenerPokemon("25") }
    }
})
