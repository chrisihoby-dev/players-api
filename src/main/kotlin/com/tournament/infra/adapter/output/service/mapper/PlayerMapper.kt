package com.tournament.infra.adapter.output.service.mapper

import com.tournament.core.port.output.InputPlayer
import com.tournament.core.port.output.OutputPlayer
import com.tournament.infra.dtos.PlayerDto
import com.tournament.infra.adapter.input.repository.model.DbPlayer
import com.tournament.infra.dtos.ResponsePlayer

fun playerDtoToInputPlayer(playerDto: PlayerDto): InputPlayer = InputPlayer(
    pseudo = playerDto.pseudo, points = playerDto.points
)


fun outputPlayerToValidPlayer(outputPlayer: OutputPlayer, rank:String?): DbPlayer = DbPlayer(
    pseudo = outputPlayer.pseudo, points = outputPlayer.points, rank = rank
)

fun dbPlayerToResponsePlayer(dbPlayer: DbPlayer): ResponsePlayer = ResponsePlayer(
    pseudo = dbPlayer.pseudo, points = dbPlayer.points, rank = dbPlayer.rank
)