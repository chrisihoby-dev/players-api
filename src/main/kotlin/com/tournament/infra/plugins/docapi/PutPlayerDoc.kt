package com.tournament.infra.plugins.docapi

import com.tournament.core.domain.errors.ErrorType
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.dtos.PlayerUpdateDto
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK

fun OpenApiRoute.putPlayerDoc() {
    summary = "Update player"
    description = "Update player from provided infos"
    request {
        pathParameter<String>("pseudo") {
            description = "Player's pseudo"
        }
        queryParameter<Boolean>("forceCreate"){
            description = "define if the creation is forced when the pseudo doesn't exist"
            required = false
        }
        body<PlayerUpdateDto> {
            description = "Contains player's infos"
            required = true
            mediaType(ContentType.Application.Json)
            example(
                "Body example", PlayerUpdateDto( points = 45, rank = "Intermediate"
                )
            )
        }
    }

    response {
        OK to {
            description = "Player update response"
            body<String>{
                example("Success response", "Player updated successfully")
            }
        }
        BadRequest to {
            description = "Bad request"
            body<List<ApiError>> {
                description = "Update Input errors"
                example(
                    "", listOf(
                        ApiError(
                            errorType = ErrorType.EMPTY_ERROR,
                            reason = "Pseudo should not be empty",
                            httpCode = 400
                        ), ApiError(
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