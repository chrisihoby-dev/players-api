package com.tournament.infra.adapter.output.service

import arrow.core.none
import arrow.core.right
import arrow.core.some
import aws.smithy.kotlin.runtime.io.IOException
import com.tournament.core.domain.errors.ErrorType.INFRA_ERROR
import com.tournament.core.domain.errors.ErrorType.UNAVAILABLE_ERROR
import com.tournament.core.usecase.PlayerUseCase
import com.tournament.infra.adapter.input.repository.DynamoPlayerRepository
import com.tournament.infra.adapter.input.repository.model.DbPlayer
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.PlayerDto
import com.tournament.infra.dtos.PlayerUpdateDto
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.FailedDependency
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.mockk.coEvery
import io.mockk.mockk

class PlayerServiceTest : DescribeSpec({

    val playerUserCase = PlayerUseCase()
    val dynamoPlayerRepository = mockk<DynamoPlayerRepository>()
    val playerService = PlayerService(playerUserCase, dynamoPlayerRepository)

    describe("addPlayer") {
        it("should add a player") {
            val player = PlayerDto(pseudo = "Spectre", points = 500, rank = "Expert")
            coEvery { dynamoPlayerRepository.doesPseudoExist(any()) } returns false
            coEvery { dynamoPlayerRepository.put(any()) } returns "Player added successfully".right()
            val actual = playerService.addPlayer(player)

            actual.onRight { it shouldBe "Player with pseudo ${player.pseudo} created successfully." }
            actual.shouldBeRight()
        }
        it("should return error when exception occurred") {
            val player = PlayerDto(pseudo = "Spectre", points = 500, rank = "Expert")
            coEvery { dynamoPlayerRepository.doesPseudoExist(any()) } returns false
            coEvery { dynamoPlayerRepository.putItem(any()) } throws IOException("Network Error")
            coEvery { dynamoPlayerRepository.put(any()) } answers { callOriginal() }
            val actual = playerService.addPlayer(player)
            actual.onLeft { errors ->
                errors.size shouldBe 1
                val expectedErrors = listOf(
                    ApiError(
                        httpCode = BadRequest.value,
                        reason = "Network Error",
                        errorType = INFRA_ERROR
                    )
                )
                errors shouldBe expectedErrors
            }
            actual.shouldBeLeft()

        }
    }

    describe("updatePlayer") {
        it("should update a player") {
            val player = PlayerUpdateDto(points = 500, rank = "Expert")
            coEvery { dynamoPlayerRepository.doesPseudoExist(any()) } returns true
            coEvery { dynamoPlayerRepository.put(any()) } returns "Player updated successfully".right()
            val actual = playerService.updatePlayer(pseudo = "MoneyPenny", playerDto = player, forceCreate = true)
            actual.onRight { it shouldBe "Player with pseudo MoneyPenny updated successfully." }
            actual.shouldBeRight()
        }
        it("should return error when exception occurred") {
            val player = PlayerUpdateDto(points = 500, rank = "Expert")
            coEvery { dynamoPlayerRepository.doesPseudoExist(any()) } returns true
            coEvery { dynamoPlayerRepository.putItem(any()) } throws IOException("Network Error")
            coEvery { dynamoPlayerRepository.put(any()) } answers { callOriginal() }
            val actual = playerService.updatePlayer(pseudo = "MoneyPenny", playerDto = player, forceCreate = false)
            actual.onLeft { errors ->
                errors.size shouldBe 1
                val expectedErrors = listOf(
                    ApiError(
                        httpCode = BadRequest.value,
                        reason = "Network Error",
                        errorType = INFRA_ERROR
                    )
                )
                errors shouldBe expectedErrors
            }
            actual.shouldBeLeft()

        }
    }

    describe("getPlayer") {
        it("should return some player when it exists") {
            coEvery { dynamoPlayerRepository.get(any()) } returns DbPlayer(
                pseudo = "Bond", points = 45,
                rank = "Genius"
            ).some()
            val actual = playerService.getPlayer("Bond")
            actual.onRight {
                it.pseudo shouldBe "Bond"
                it.points shouldBe 45
                it.rank shouldBe "Genius"
            }
            actual.shouldBeRight()
        }
        it("should return error when it doesn't exist") {
            coEvery { dynamoPlayerRepository.get(any()) } returns none()
            val actual = playerService.getPlayer("Bond")
            actual.onLeft { errors ->
                errors.first().let {
                    it.reason shouldBe "Pseudo: Bond is not found."
                    it.errorType shouldBe UNAVAILABLE_ERROR
                    it.httpCode shouldBe NotFound.value
                }
            }
            actual.shouldBeLeft()
        }
    }

    describe("deletePlayer") {
        it("should delete a player") {
            coEvery { dynamoPlayerRepository.get(any()) } returns DbPlayer(
                pseudo = "Bond", points = 45,
                rank = "Genius"
            ).some()
            coEvery { dynamoPlayerRepository.deleteItem(any()) } returns Unit
            coEvery { dynamoPlayerRepository.delete(any()) } answers { callOriginal() }
            val actual = playerService.deletePlayer("Bond")
            actual.onRight { it shouldBe "Player Bond successfully removed." }
            actual.shouldBeRight()
        }
        it("should return error when exception occurred") {
            coEvery { dynamoPlayerRepository.get(any()) } returns DbPlayer(
                pseudo = "Bond", points = 45,
                rank = "Genius"
            ).some()
            coEvery { dynamoPlayerRepository.deleteItem(any()) } throws IOException("Network Error")
            coEvery { dynamoPlayerRepository.delete(any()) } answers { callOriginal() }
            val actual = playerService.deletePlayer("Bond")
            actual.onLeft { errors ->
                errors.first().let {
                    it.reason shouldBe "Network Error"
                    it.httpCode shouldBe BadRequest.value
                    it.errorType shouldBe INFRA_ERROR
                }
            }
            actual.shouldBeLeft()

        }
    }

    describe("getAllPlayers") {
        it("should return some players ordered desc by points when it exists") {
            coEvery { dynamoPlayerRepository.all() } returns listOf(
                DbPlayer("Bond", 99, "Expert"),
                DbPlayer("008", 1, "Dummy"),

                ).right()
            val actual = playerService.getAllPlayers(sortBy = "points")
            actual.onRight {
                it.size shouldBe 2
                it.first().points shouldBe 99
            }
            actual.shouldBeRight()
        }
        it("should return some players ordered desc by pseudo when it exists") {
            coEvery { dynamoPlayerRepository.all() } returns listOf(
                DbPlayer("Bond", 99, "Expert"),
                DbPlayer("008", 1, "Dummy"),
            ).right()
            val actual = playerService.getAllPlayers(sortBy = "pseudo")
            actual.onRight {
                it.size shouldBe 2
                it.first().points shouldBe 99
            }
            actual.shouldBeRight()
        }
        it("should return some players ordered desc by rank when it exists") {
            coEvery { dynamoPlayerRepository.all() } returns listOf(
                DbPlayer("Bond", 99, "Expert"),
                DbPlayer("008", 1, "Dummy"),
            ).right()
            val actual = playerService.getAllPlayers(sortBy = "rank")
            actual.onRight {
                it.size shouldBe 2
                it.first().points shouldBe 99
            }
            actual.shouldBeRight()
        }
        it("should return error when exception occurred") {
            coEvery { dynamoPlayerRepository.scanItem() } throws IOException("Network Error")
            coEvery { dynamoPlayerRepository.all() } answers { callOriginal() }
            val actual = playerService.getAllPlayers(sortBy = "points")
            actual.onLeft { errors ->
                errors.first().let {
                    it.reason shouldBe "Network Error"
                    it.errorType shouldBe INFRA_ERROR
                    it.httpCode shouldBe FailedDependency.value
                }
            }
        }
    }
})
