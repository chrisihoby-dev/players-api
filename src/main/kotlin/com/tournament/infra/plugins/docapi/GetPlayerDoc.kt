package com.tournament.infra.plugins.docapi

import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.ResponsePlayer
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK

fun OpenApiRoute.getPlayerDoc() {
    summary = "Get one player"
    description = "Get one player for the given pseudo"
    request {
        pathParameter<String>("pseudo") {
            description = "Player's pseudo"
        }
    }

    response {
        OK to {
            description = "Player response"
            body<ResponsePlayer>{
                example(
                    "Success Response", ResponsePlayer(
                        pseudo = "This is a pseudo", points = 15, rank = "Rookie"
                    )
                )
            }
        }
        NotFound to {
            description = "Player not found"
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
                description = "Parsing error"
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