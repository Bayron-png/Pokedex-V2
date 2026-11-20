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
        // Dado: un Repository falso que devuelve una ficha de Pikachu.
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

    test("buscar por número y luego por nombre llama a la API una sola vez") {
        // Dado: la API solo sabe responder a "25".
        val api = mockk<PokeApi>()
        coEvery { api.obtenerPokemon("25") } returns ficha
        val repo = PokemonRepository(api)

        // Cuando: se busca por número y después por nombre.
        repo.buscar("25") shouldBe ficha
        repo.buscar("pikachu") shouldBe ficha

        // Entonces: la segunda salió de la caché; la API se usó una sola vez.
        coVerify(exactly = 1) { api.obtenerPokemon(any()) }
    }
})
