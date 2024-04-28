package com.tournament.plugins

import com.tournament.core.domain.errors.ErrorType.UNAVAILABLE_ERROR
import com.tournament.core.domain.errors.ErrorType.VALUE_CONDITION_ERROR
import com.tournament.infra.adapter.input.repository.model.DbPlayer
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.PlayerDto
import com.tournament.infra.dtos.PlayerUpdateDto
import com.tournament.infra.extensions.createTestHttpClient
import com.tournament.infra.plugins.configureContext
import com.tournament.infra.plugins.configureRouting
import com.tournament.infra.plugins.configureSerialization
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

private const val CONTAINER_PORT = 8000


private const val HTTP = "http"

class PlayerRoutingITTest : FunSpec({
    val dynamoDbContainer =
        GenericContainer(
            DockerImageName.parse("amazon/dynamodb-local:latest")
        ).apply {
            withExposedPorts(CONTAINER_PORT)
            isHostAccessible = true
            waitingFor(Wait.forListeningPorts(CONTAINER_PORT))
        }
    dynamoDbContainer.start()
    Thread.sleep(3000)
    val hostPort = dynamoDbContainer.getMappedPort(CONTAINER_PORT)
    val hostIP = dynamoDbContainer.host
    val endPointUrl = "$HTTP://$hostIP:$hostPort"



    test("get should return the correct response") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.get("/player/Bond") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<DbPlayer>()
            response.status shouldBe HttpStatusCode.OK
            responseBody.pseudo shouldBe "Bond"
        }

    }

    test("get all should return the correct response") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.get("/player/all") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<List<DbPlayer>>()
            response.status shouldBe HttpStatusCode.OK
            responseBody.size shouldBe 2

        }

    }

    test("get should return error when pseudo doesn't exist") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.get("/player/Vesper") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<List<ApiError>>()
            response.status shouldBe HttpStatusCode.NotFound
            responseBody.size shouldBe 1
            responseBody.first().let {
                it.reason shouldBe "Pseudo: Vesper is not found."
                it.httpCode shouldBe HttpStatusCode.NotFound.value
                it.errorType shouldBe UNAVAILABLE_ERROR
            }
        }

    }

    test("post should return correct response when the provided input is correct") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.post("/player") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerDto(
                        pseudo = "008", points = 2, rank = "Beginner"
                    )
                )
            }
            val responseBody = response.bodyAsText()
            response.status shouldBe HttpStatusCode.Created
            responseBody shouldBe "Player with pseudo 008 created successfully."
        }

    }

    test("post should return errors when the provided input is incorrect") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.post("/player") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerDto(
                        pseudo = "", points = -2, rank = "Beginner"
                    )
                )
            }
            val responseBody = response.body<List<ApiError>>()
            val expectedErrors = listOf(
                "pseudo can't be empty", "points must have positive value"
            )
            response.status shouldBe HttpStatusCode.BadRequest
            responseBody.size shouldBe 2
            responseBody.forEach { error -> expectedErrors.any { it == error.reason } }
        }

    }

    test("put should return correct response when the provided input is correct") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.put("/player/Bond") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerUpdateDto(
                        points = 999, rank = "Expert"
                    )
                )
            }
            val responseBody = response.bodyAsText()
            response.status shouldBe HttpStatusCode.Created
            responseBody shouldBe "Player with pseudo Bond updated successfully."
        }

    }
    test("put should return errors when the provided points has negative value") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.put("/player/Bond") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerUpdateDto(
                        points = -12, rank = "Expert"
                    )
                )
            }
            val responseBody = response.body<List<ApiError>>()
            response.status shouldBe HttpStatusCode.BadRequest
            responseBody.size shouldBe 1
            responseBody.first().let {
                it.reason shouldBe "points must have positive value"
                it.errorType shouldBe VALUE_CONDITION_ERROR
            }
        }

    }

    test("put should return errors when the provided pseudo doesn't exist and forceCreate not true") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.put("/player/M") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerUpdateDto(
                        points = 812, rank = "Expert"
                    )
                )
            }
            val responseBody = response.body<List<ApiError>>()
            response.status shouldBe HttpStatusCode.BadRequest
            responseBody.size shouldBe 1
            responseBody.first().let {
                it.reason shouldBe "Pseudo value = M does not exist. Set the forceCreate query parameter to true to force creation."
                it.errorType shouldBe UNAVAILABLE_ERROR
            }
        }
    }

    test("put should return success response when the provided pseudo doesn't exist and forceCreate is set to true") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.put("/player/M?forceCreate=true") {
                contentType(ContentType.Application.Json)
                setBody(
                    PlayerUpdateDto(
                        points = 812, rank = "Expert"
                    )
                )
            }
            val responseBody = response.bodyAsText()
            response.status shouldBe HttpStatusCode.Created
            responseBody shouldBe "Player with pseudo M updated successfully."

        }

    }

    test("delete should return success response when pseudo exists") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.delete("/player/Bond") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.bodyAsText()
            response.status shouldBe HttpStatusCode.OK
            responseBody shouldBe "Player Bond successfully removed."
        }

    }

    test("delete should return error response when pseudo doesn't exist") {
        testApplication {

            val clientTest = createTestHttpClient()
            application {
                configureContext(endPointUrl)
                configureSerialization()
                configureRouting()
            }
            environment {
                config = MapApplicationConfig()
            }
            val response = clientTest.delete("/player/Spectre") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<List<ApiError>>()
            val expectedErrors = listOf(
                "Pseudo: Spectre is not found."
            )
            response.status shouldBe HttpStatusCode.NotFound
            responseBody.forEach { error -> expectedErrors.any { it == error.reason } }
        }

    }


    /*
        afterEach {
            dynamoDbContainer.stop()
        }*/


})
