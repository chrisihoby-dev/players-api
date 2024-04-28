package com.tournament.infra.adapter.input.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class DbPlayer(val pseudo: String, val points: Int, val rank: String? = null)

enum class SortMode(val value:String) {
    PSEUDO("pseudo"), POINTS("points"), RANK("rank")
}