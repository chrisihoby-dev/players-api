package com.tournament.infra.adapter.input.repository

import arrow.core.EitherNel
import arrow.core.Option

import com.tournament.infra.adapter.input.repository.model.DbPlayer
import com.tournament.infra.adapter.output.service.errors.ApiError


interface PlayerDao {
    suspend fun put(dbPlayer: DbPlayer):EitherNel<ApiError, String>
    suspend fun delete(pseudo:String):EitherNel<ApiError, String>
    suspend fun get(pseudo:String): Option<DbPlayer>
    suspend fun all(): EitherNel<ApiError, List<DbPlayer>>
}