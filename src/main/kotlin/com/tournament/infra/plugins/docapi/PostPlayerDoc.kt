package com.tournament.infra.plugins.docapi

import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.PlayerDto
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK

fun OpenApiRoute.postPlayerDoc() {
    summary = "Create player"
    description = "Create player from provided infos"
    request {
        body<PlayerDto> {
            description = "Contains player's infos"
            required = true
            mediaType(ContentType.Application.Json)
            example(
                "Body example", PlayerDto(pseudo = "pseudo", points = 45, rank = "Intermediate"
                )
            )
        }
    }

    response {
        OK to {
            description = "Player creation response"
            body<String>{
                example("Success response", "Player created successfully")
            }
        }
        BadRequest to {
            description = "Bad request"
            body<List<ApiError>> {
                description = "Creation Input errors"
                example(
                    "", listOf(
                        ApiError(
                            errorType = ErrorType.EMPTY_ERROR,
                            reason = "Pseudo should not be empty",
                            httpCode = 400
                        ),ApiError(
                            errorType = ErrorType.VALUE_CONDITION_ERROR,
                            reason = "points must have positive value",
                            httpCode = 400
                        )
                    )
                )
            }
        }


    }
}