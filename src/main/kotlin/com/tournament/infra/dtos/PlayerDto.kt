package com.tournament.infra.dtos

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
@Schema(title = "Player Information", description = "Player Information"   )
data class PlayerDto(
    @field:Schema(description = "player's pseudo", required = true)
    val pseudo: String? = null,
    @field:Schema(description = "player's points", required = true)
    val points: Int? = null,
    @field:Schema(description = "player's rank", required = true)
    val rank: String? = null)
@Serializable
@Schema(title = "Player Information to update", description = "Player Information"   )
data class PlayerUpdateDto(
    @field:Schema(description = "player's points", required = true)
    val points: Int? = null,
    @field:Schema(description = "player's rank", required = true)
    val rank: String? = null
)
@Serializable
@Schema(title = "Player Response",
    description = "Represents player.")
data class ResponsePlayer(
    @field:Schema(description = "player's pseudo", required = true)
    val pseudo: String,
    @field:Schema(description = "player's points", required = true)
    val points: Int,
    @field:Schema(description = "player's rank", required = true)
    val rank: String? = null)

