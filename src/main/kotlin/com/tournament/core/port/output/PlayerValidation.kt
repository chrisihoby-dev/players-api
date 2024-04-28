package com.tournament.core.port.output

import arrow.core.EitherNel
import com.tournament.core.domain.errors.DomainError
import com.tournament.core.port.input.DoesPseudoExistFn

interface PlayerValidation {
    suspend fun validatePlayerCreation(
        inputPlayer: InputPlayer,
        doesPseudoExistFn: DoesPseudoExistFn
    ): EitherNel<DomainError, OutputPlayer>
    suspend fun validatePlayerUpdate(
        inputPlayer: InputPlayer, doesPseudoExistFn: DoesPseudoExistFn,
        forceCreate: Boolean
    ): EitherNel<DomainError, OutputPlayer>
}

data class InputPlayer(val pseudo:String?, val points:Int?)
data class OutputPlayer(val pseudo:String, val points:Int)