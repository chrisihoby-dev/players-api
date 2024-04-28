package com.tournament.core.domain.entity

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import arrow.core.right

import com.tournament.core.domain.errors.DomainError
import com.tournament.core.domain.errors.ErrorType.BLANK_ERROR
import com.tournament.core.domain.errors.ErrorType.EMPTY_ERROR
import com.tournament.core.domain.errors.ErrorType.NULL_ERROR
import com.tournament.core.domain.errors.ErrorType.VALUE_CONDITION_ERROR
import com.tournament.core.port.output.InputPlayer

data class PlayerEntity(val pseudo: FilledText, val points: PositiveNumber){
    companion object{
        fun build(inputPlayer: InputPlayer):EitherNel<DomainError, PlayerEntity> = either {
            zipOrAccumulate(
                {
                   FilledText.build(inputPlayer.pseudo, "pseudo").bindNel()
                },
                {
                   PositiveNumber.build(inputPlayer.points, "points").bindNel()
                }
            ){pseudo, points ->
                PlayerEntity(pseudo, points)
            }
        }
    }
}

@JvmInline
value class FilledText(val text: String) {
    companion object {
        fun build(value: String?, fieldName: String = "value"): EitherNel<DomainError, FilledText> = value?.let {
            when {
                it.isEmpty() -> DomainError(
                    errorType = EMPTY_ERROR,
                    reason = "$fieldName can't be empty"
                ).leftNel()

                it.isBlank() -> DomainError(
                    errorType = BLANK_ERROR,
                    reason = "$fieldName can't be blank"
                ).leftNel()

                else -> FilledText(it).right()
            }
        } ?: DomainError(
            errorType = NULL_ERROR,
            reason = "$fieldName can't be null"
        ).leftNel()
    }
}

@JvmInline
value class PositiveNumber(val value: Int){
    companion object {
        fun build(value: Int?, fieldName: String = "value"):EitherNel<DomainError, PositiveNumber> =
            value?.let {
              if(it >= 0) PositiveNumber(it).right() else  DomainError(
                  errorType = VALUE_CONDITION_ERROR,
                  reason = "$fieldName must have positive value"
              ).leftNel()
            }?: DomainError(errorType = NULL_ERROR, reason = "$fieldName should not be null").leftNel()
    }
}
