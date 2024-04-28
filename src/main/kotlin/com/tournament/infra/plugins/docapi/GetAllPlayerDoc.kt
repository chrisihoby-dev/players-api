package com.tournament.infra.plugins.docapi

import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.ResponsePlayer
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK

fun OpenApiRoute.getAllPlayerDoc() {
    summary = "Get all player"
    description = "Get all player"
    request {
        queryParameter<String>("sortBy"){
            description = "sort by fields"
            required = false
        }
    }

    response {
        OK to {
            description = "Player response"
            body<List<ResponsePlayer>>{
                example(
                    "Success Response", listOf(
                        ResponsePlayer(
                            pseudo = "pseudo1", points = 15, rank = "Rookie"
                        ), ResponsePlayer(
                            pseudo = "pseudo2", points = 16, rank = "Rookie"
                        ),
                    )
                )
            }
        }
        BadRequest to {
            description = "Bad request"
            body<List<ApiError>> {
                description = "Parsing error"
                example(
                    "", listOf(
                        ApiError(
                            errorType = ErrorType.PARSING_ERROR,
                            reason = "failed query",
                            httpCode = 400
                        )
                    )
                )
            }
        }


    }
}