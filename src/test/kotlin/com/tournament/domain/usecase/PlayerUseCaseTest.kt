package com.tournament.domain.usecase

import com.tournament.core.port.output.InputPlayer
import com.tournament.core.usecase.PlayerUseCase
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PlayerUseCaseTest : DescribeSpec({
    val playerUserCase = PlayerUseCase()
    describe("validatePlayerCreation") {

        it("Should return a valid player when provided player is correct") {
            // GIVEN
            val player = InputPlayer(pseudo = "user1", points = 152)
            // WHEN
            val actual = playerUserCase.validatePlayerCreation(player) { false }

            // THEN
            actual.onRight {
                it.pseudo shouldBe "user1"
                it.points shouldBe 152
            }

            actual.shouldBeRight()
        }
        it("Should return error when pseudo not provided and points not provided") {
            // GIVEN
            val player = InputPlayer(pseudo = null, points = null)
            // WHEN
            val actual = playerUserCase.validatePlayerCreation(player) { false }

            // THEN
            val expectedErrors = listOf(
                "pseudo can't be null", "points should not be null"
            )
            actual.onLeft { errors ->
                errors.size shouldBe 2
                errors.map { it.reason }.all { expectedErrors.contains(it) }
            }
            actual.shouldBeLeft()
        }

        it("Should return error when pseudo is blank and points has negative value") {
            // GIVEN
            val player = InputPlayer(pseudo = " ", points = -129)
            // WHEN
            val actual = playerUserCase.validatePlayerCreation(player) { false }

            // THEN
            val expectedErrors = listOf(
                "pseudo can't be blank", "points must have positive value"
            )
            actual.onLeft { errors ->
                errors.size shouldBe 2
                errors.map { it.reason }.all { expectedErrors.contains(it) }
            }
            actual.shouldBeLeft()
        }

        it("Should return error when pseudo is empty ") {
            // GIVEN
            val player = InputPlayer(pseudo = "", points = 129)
            // WHEN
            val actual = playerUserCase.validatePlayerCreation(player) { false }

            // THEN
            val expectedErrors = listOf(
                "pseudo can't be empty"
            )
            actual.onLeft { errors ->
                errors.size shouldBe 1
                errors.map { it.reason }.all { expectedErrors.contains(it) }
            }
            actual.shouldBeLeft()
        }

        it("Should return error when pseudo already exists ") {
            // GIVEN
            val player = InputPlayer(pseudo = "user1", points = 129)
            // WHEN
            val actual = playerUserCase.validatePlayerCreation(player) { true }

            // THEN
            val expectedErrors = listOf(
                "Pseudo value = user1 already exists and is not available anymore."
            )
            actual.onLeft { errors ->
                errors.size shouldBe 1
                errors.map { it.reason }.all { expectedErrors.contains(it) }
            }
            actual.shouldBeLeft()
        }

    }

    describe("validatePlayerUpdate") {
        it("Should return a valid player when player infos is correct") {
            // GIVEN
            val player = InputPlayer(pseudo = "user1", points = 152)
            // WHEN
            val actual = playerUserCase.validatePlayerUpdate(
                inputPlayer = player,
                doesPseudoExistFn = { true },
                forceCreate = false
            )

            // THEN
            actual.onRight {
                it.pseudo shouldBe "user1"
                it.points shouldBe 152
            }

            actual.shouldBeRight()
        }

        it("Should return error when pseudo doesn't exist and forceCreate is false ") {
            // GIVEN
            val player = InputPlayer(pseudo = "user1", points = 129)
            // WHEN
            val actual = playerUserCase.validatePlayerUpdate(
                inputPlayer = player,
                doesPseudoExistFn = { false },
                forceCreate = false
            )

            // THEN
            val expectedErrors = listOf(
                "Pseudo value = user1 does not exist. Set the forceCreate query parameter to true to force creation."
            )
            actual.onLeft { errors ->
                errors.size shouldBe 1
                errors.map { it.reason }.all { expectedErrors.contains(it) }
            }
            actual.shouldBeLeft()
        }

        it("Should return valid player when pseudo doesn't exist and forceCreate is true ") {
            // GIVEN
            val player = InputPlayer(pseudo = "user1", points = 129)
            // WHEN
            val actual = playerUserCase.validatePlayerUpdate(
                inputPlayer = player,
                doesPseudoExistFn = { false },
                forceCreate = true
            )

            // THEN

            actual.onRight {
                it.pseudo shouldBe "user1"
                it.points shouldBe 129
            }
            actual.shouldBeRight()
        }

    }
})
