package com.tournament.infra.adapter.output.service.errors

import arrow.core.NonEmptyList
import com.tournament.core.domain.errors.CommonError
import com.tournament.core.domain.errors.DomainError
import com.tournament.core.domain.errors.ErrorType
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

/**
 * Represents an input error.
 *
 * @property reason The reason for the error.
 * @property httpCode The HTTP code associated with the error (optional).
 */
@Serializable
@Schema(
    title = "Rest API error",
    description = "Represents an api error."
)
data class ApiError(
    @field:Schema(description = "The type of error.", required = true)
    val errorType: ErrorType,
    @field:Schema(description = "The reason for the error.", required = true)
    val reason: String,
    @field:Schema(description = "The HTTP code associated with the error", required = false)
    val httpCode: Int? = null
)


typealias DomainToApiErrorsFn = (NonEmptyList<CommonError>) -> NonEmptyList<ApiError>

/**
 * Maps a list of [CommonError] errors to a list of [ApiError] errors.
 *
 * @return A function that takes a [NonEmptyList] of [CommonError] errors as input and returns a [NonEmptyList] of [ApiError] errors.
 * The function maps each [CommonError] to an [ApiError] based on its type.
 */
fun domainToAppErrors(): DomainToApiErrorsFn = { errors ->
    errors.map {
        when (val error = it) {
            is DomainError -> {
                ApiError(reason = error.reason, errorType = error.errorType)
            }
        }


    }
}