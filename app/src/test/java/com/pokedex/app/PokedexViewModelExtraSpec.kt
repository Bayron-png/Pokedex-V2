package com.pokedex.app

import com.pokedex.app.data.model.*
import com.pokedex.app.repository.PokemonRepository
import com.pokedex.app.viewmodel.PokedexViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexViewModelExtraSpec : FunSpec({
    // Ficha de ejemplo de Pikachu.
    val ficha = PokemonDto(
        25,
        "pikachu",
        4,
        60,
        SpritesDto(null),
        listOf(PokemonTypeDto(1, TypeDto("electric")))
    )

    // Dispatcher: Es el encargado de determinar dónde y cuándo se ejecutan las corrutinas.
    val dispatcher = StandardTestDispatcher()

    // Configurar el hilo principal para las pruebas.
    beforeTest {
        Dispatchers.setMain(dispatcher)
    }

    // Restaurar el hilo principal después de cada prueba.
    afterTest {
        Dispatchers.resetMain()
    }

    test("actualizarConsulta limpia la ficha y el error anteriores") {
        // Dado: un Repository falso que devuelve una ficha de Pikachu.
        val repo = mockk<PokemonRepository>()

        // Cada vez que el nombre sea pikachu, debe retornar la ficha de Pikachu
        coEvery {
            repo.buscar("pikachu")
        } returns ficha

        val vm = PokedexViewModel(repo)

        // Primero se realiza una búsqueda exitosa.
        runTest(dispatcher) {
            vm.buscar()
            advanceUntilIdle() // Ejecuta corrutinas hasta que no quede ninguna por ejecutar
        }

        // Comprobar que existe una ficha antes de actualizar la consulta.
        vm.uiState.value.pokemon shouldBe ficha

        // Escribir otro Pokémon.
        vm.actualizarConsulta("charizard")

        // Entonces: se actualiza el texto y se elimina la ficha anterior.
        vm.uiState.value.consulta shouldBe "charizard"
        vm.uiState.value.pokemon.shouldBeNull()
        vm.uiState.value.error.shouldBeNull()

        // Provocar un error mediante una consulta vacía.
        vm.actualizarConsulta("")
        vm.buscar()

        vm.uiState.value.error shouldBe "Escribe un nombre o número."

        // Consulta final
        vm.actualizarConsulta("bulbasaur")

        // El error anterior también debe desaparecer.
        vm.uiState.value.consulta shouldBe "bulbasaur"
        vm.uiState.value.error.shouldBeNull()
        vm.uiState.value.pokemon.shouldBeNull()
    }
})
