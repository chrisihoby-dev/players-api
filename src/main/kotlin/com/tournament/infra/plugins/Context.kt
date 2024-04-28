package com.tournament.infra.plugins


import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.net.url.Url.Companion.parse
import com.tournament.core.port.output.PlayerValidation
import com.tournament.core.usecase.PlayerUseCase
import com.tournament.infra.adapter.input.repository.DynamoPlayerRepository
import com.tournament.infra.adapter.input.repository.PlayerDao
import com.tournament.infra.adapter.input.repository.createTableRequest
import com.tournament.infra.adapter.input.repository.initDatabase
import com.tournament.infra.adapter.output.service.PlayerService
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.host
import io.ktor.server.application.install
import io.ktor.server.application.port
import kotlinx.coroutines.runBlocking
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.KoinIsolated
import org.koin.logger.slf4jLogger

const val PLAYER_TABLE_NAME = "players"

const val PSEUDO_ATTRIBUTE = "pseudo"

fun configAppModule(dbEndPointUrl: String) = module {
    single(createdAtStart = true) {
        val dynamoDbClient = DynamoDbClient {
            region = "us-west-3"
            credentialsProvider = StaticCredentialsProvider.invoke {
                accessKeyId = "AKIAIOSFODNN7EXAMPLE"
                secretAccessKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
                sessionToken = "FAKE_SESSION_TOKEN"
            }
            endpointUrl = parse(dbEndPointUrl)
        }
        runBlocking(block = initDatabase(dynamoDbClient, createTableRequest))
        dynamoDbClient
    }
    single { PlayerUseCase() } bind PlayerValidation::class
    single { DynamoPlayerRepository(get()) } bind PlayerDao::class
    single { PlayerService(get(),get()) }
}



private const val DEFAULT_DB_ENDPOINT = "http://localhost:8000"

fun Application.configureContext(dbEndPointUrl: String ?= null) {
    environment.config.host
    val realUrl = dbEndPointUrl?:environment.config.propertyOrNull("database.endpoint")?.getString() ?: DEFAULT_DB_ENDPOINT
    install(KoinIsolated) {
        slf4jLogger()
        modules(configAppModule(realUrl))

    }
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger-ui"
            forwardRoot = true
        }
        info {
            title = "Tournament API"
            version = "latest"
            description = "Contains API REST for players"
        }
        server {
            url = "http://localhost:${environment.config.port}"
            description = "Player Service"
        }



    }
}