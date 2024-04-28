package com.tournament.infra.plugins.docapi


import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.adapter.output.service.errors.ApiError
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK

fun OpenApiRoute.deletePlayerDoc() {
    summary = "Delete one player"
    description = "Delete one player for the given pseudo"
    request {
        pathParameter<String>("pseudo") {
            description = "Player's pseudo to delete"
        }
    }

    response {
        OK to {
            description = "Player deletion response"
            body<String>{
                example(
                    "Success Response", "Player successfully deleted"
                )
            }
        }
        NotFound to {
            description = "Player to delete not found"
            body<List<ApiError>> {
                description = "Player not found"
                example(
                    "Not found error", listOf(
                        ApiError(
                            errorType = ErrorType.UNAVAILABLE_ERROR,
                            reason = "Player X is not found",
                            httpCode = 404
                        )
                    )
                )
            }
        }
        BadRequest to {
            description = "Bad request"
            body<List<ApiError>> {
                description = "Deletion Parsing error"
                example(
                    "bad request error", listOf(
                        ApiError(
                            errorType = ErrorType.PARSING_ERROR,
                            reason = "pseudo not provided",
                            httpCode = 400
                        )
                    )
                )
            }
        }


    }
}