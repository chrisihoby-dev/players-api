package com.tournament.infra.extensions

import arrow.core.raise.catch
import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.plugins.jsonConfig
import com.tournament.infra.adapter.output.service.errors.ApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.testing.ApplicationTestBuilder

/**
 * Receives the request body and handles any exceptions that may occur during deserialization.
 *
 * @return The deserialized object of type [T], or null if an exception occurs during deserialization.
 *         If an exception occurs, an HTTP response with status code [HttpStatusCode.BadRequest] and an [InputError] object
 *         containing the error details will be sent as the response.
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveWithExceptionHandling(): T? =
    catch({ this.receiveNullable<T>() }) {
        this.respond(
            HttpStatusCode.BadRequest, ApiError(
                reason = it.cause?.localizedMessage ?: it.localizedMessage,
                errorType = ErrorType.PARSING_ERROR,
            )
        )
        null
    }

/**
 * Creates a test HttpClient with specific configurations installed.
 *
 * @return The test HttpClient.
 */
fun ApplicationTestBuilder.createTestHttpClient() = createClient {

    this.install(ContentNegotiation) {
        json(jsonConfig)
    }
}