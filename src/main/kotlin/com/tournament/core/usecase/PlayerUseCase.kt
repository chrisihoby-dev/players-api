package com.tournament.core.usecase

import arrow.core.EitherNel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.tournament.core.domain.entity.PlayerEntity
import com.tournament.core.domain.errors.DomainError
import com.tournament.core.domain.errors.ErrorType.UNAVAILABLE_ERROR
import com.tournament.core.port.input.DoesPseudoExistFn
import com.tournament.core.port.output.InputPlayer
import com.tournament.core.port.output.OutputPlayer
import com.tournament.core.port.output.PlayerValidation


class PlayerUseCase : PlayerValidation {

    override suspend fun validatePlayerCreation(
        inputPlayer: InputPlayer,
        doesPseudoExistFn: DoesPseudoExistFn
    ): EitherNel<DomainError, OutputPlayer> = either {
        val player = PlayerEntity.build(inputPlayer).bind()
        val pseudo = player.pseudo.text
        ensure(!doesPseudoExistFn(pseudo)) {
            nonEmptyListOf(
                DomainError(errorType = UNAVAILABLE_ERROR, "Pseudo value = $pseudo already exists and is not available anymore.")
            )
        }
        OutputPlayer(player.pseudo.text, player.points.value)
    }

    override suspend fun validatePlayerUpdate(
        inputPlayer: InputPlayer, doesPseudoExistFn: DoesPseudoExistFn,
        forceCreate: Boolean
    ): EitherNel<DomainError, OutputPlayer> = either {
        val player = PlayerEntity.build(inputPlayer).bind()
        val pseudo = player.pseudo.text
        ensure(doesPseudoExistFn(pseudo) || forceCreate) {
            nonEmptyListOf(
                DomainError(
                    errorType = UNAVAILABLE_ERROR,
                    "Pseudo value = $pseudo does not exist. Set the forceCreate query parameter to true to force creation.")
            )
        }
        OutputPlayer(player.pseudo.text, player.points.value)

    }

}