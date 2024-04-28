package com.tournament.infra.adapter.output.service

import arrow.core.EitherNel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.tournament.core.domain.errors.ErrorType
import com.tournament.core.port.output.InputPlayer
import com.tournament.core.port.output.PlayerValidation
import com.tournament.infra.adapter.input.repository.DynamoPlayerRepository
import com.tournament.infra.adapter.input.repository.PlayerDao
import com.tournament.infra.adapter.input.repository.model.SortMode
import com.tournament.infra.adapter.input.repository.model.SortMode.POINTS
import com.tournament.infra.adapter.input.repository.model.SortMode.PSEUDO
import com.tournament.infra.adapter.input.repository.model.SortMode.RANK
import com.tournament.infra.adapter.input.repository.model.SortMode.entries
import com.tournament.infra.adapter.output.service.errors.ApiError
import com.tournament.infra.adapter.output.service.errors.domainToAppErrors
import com.tournament.infra.adapter.output.service.mapper.dbPlayerToResponsePlayer
import com.tournament.infra.adapter.output.service.mapper.outputPlayerToValidPlayer
import com.tournament.infra.adapter.output.service.mapper.playerDtoToInputPlayer
import com.tournament.infra.dtos.PlayerDto
import com.tournament.infra.dtos.PlayerUpdateDto
import com.tournament.infra.dtos.ResponsePlayer
import io.ktor.http.HttpStatusCode

class PlayerService(
    private val playerUserCase: PlayerValidation,
    private val playerDao: PlayerDao
) {

    suspend fun addPlayer(playerDto: PlayerDto): EitherNel<ApiError, String> = either {
        val outputPlayer = playerUserCase.validatePlayerCreation(
            inputPlayer = playerDtoToInputPlayer(playerDto),
            doesPseudoExistFn = (playerDao as DynamoPlayerRepository)::doesPseudoExist
        )
        val creationState = outputPlayer.map { "Player with pseudo ${it.pseudo} created successfully." }
            .mapLeft(domainToAppErrors()).bind()
        val value = outputPlayer.mapLeft(domainToAppErrors()).bind()
        playerDao.put(outputPlayerToValidPlayer(value, playerDto.rank)).bind()
        creationState
    }

    suspend fun updatePlayer(
        pseudo: String?, playerDto: PlayerUpdateDto,
        forceCreate: Boolean
    ): EitherNel<ApiError, String> = either {
        val outputPlayer = playerUserCase.validatePlayerUpdate(
            inputPlayer = InputPlayer(pseudo, playerDto.points),
            doesPseudoExistFn = (playerDao as DynamoPlayerRepository)::doesPseudoExist, forceCreate = forceCreate
        )
        val updateState = outputPlayer.map { "Player with pseudo $pseudo updated successfully." }
            .mapLeft(domainToAppErrors()).bind()
        val value = outputPlayer.mapLeft(domainToAppErrors()).bind()
        playerDao.put(outputPlayerToValidPlayer(value, playerDto.rank)).bind()
        updateState
    }

    suspend fun getPlayer(pseudo: String): EitherNel<ApiError, ResponsePlayer> = either {
        val player = playerDao.get(pseudo)
        ensure(player.isSome()) {
            nonEmptyListOf(
                ApiError(
                    errorType = ErrorType.UNAVAILABLE_ERROR,
                    reason = "Pseudo: $pseudo is not found.",
                    httpCode = HttpStatusCode.NotFound.value
                )
            )
        }
        player.getOrNull()?.let(::dbPlayerToResponsePlayer)!!
    }

    suspend fun deletePlayer(playerName: String): EitherNel<ApiError, String> = either {
        val validPlayer = getPlayer(playerName).bind()
        playerDao.delete(validPlayer.pseudo).bind()
    }

    suspend fun getAllPlayers(sortBy: String?): EitherNel<ApiError, List<ResponsePlayer>> = either {
        if (entries.map { it.value }.contains(sortBy)) {
            when (SortMode.valueOf(sortBy?.uppercase() ?: POINTS.name)) {
                PSEUDO -> playerDao.all().bind().map(::dbPlayerToResponsePlayer).sortedByDescending { it.pseudo }
                POINTS -> playerDao.all().bind().map(::dbPlayerToResponsePlayer).sortedByDescending { it.points }
                RANK -> playerDao.all().bind().map(::dbPlayerToResponsePlayer).sortedByDescending { it.rank }
            }
        } else {
            playerDao.all().bind().map(::dbPlayerToResponsePlayer).sortedByDescending { it.points }
        }

    }


}