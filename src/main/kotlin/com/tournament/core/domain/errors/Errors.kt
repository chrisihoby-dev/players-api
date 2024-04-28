package com.tournament.core.domain.errors

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Represents a domain error that can occur during execution.
 *
 * @property errorType The type of the error.
 * @property reason Details about the error.
 */
@Schema(title = "Domain Error")
data class DomainError(
    @field:Schema(description = "Error type",
        allowableValues = ["EMPTY_ERROR","BLANK_ERROR","NULL_ERROR",
            "VALUE_CONDITION_ERROR","NOT_FOUND_ERROR","UNKNOWN_ERROR"])
    val errorType: ErrorType,
    @field:Schema(description = "Details about the error", required = true)
    val reason: String
) : CommonError()

sealed class CommonError


/**
 * Represents the types of errors that can occur during execution.
 *
 * @property value The string value associated with the error type.
 *
 * @see DomainError
 * @see CommonError
 */
enum class ErrorType(private val value: String) {
    EMPTY_ERROR("EMPTY_ERROR"),
    BLANK_ERROR("BLANK_ERROR"),
    NULL_ERROR("NULL_ERROR"),
    VALUE_CONDITION_ERROR("VALUE_CONDITION_ERROR"),
    UNAVAILABLE_ERROR("UNAVAILABLE_ERROR"),
    INFRA_ERROR("INFRA_ERROR"),
    PARSING_ERROR("PARSING_ERROR")
}