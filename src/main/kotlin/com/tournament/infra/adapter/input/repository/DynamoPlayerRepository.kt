package com.tournament.infra.adapter.input.repository

import arrow.core.EitherNel
import arrow.core.Option
import arrow.core.Option.Companion.fromNullable
import arrow.core.leftNel
import arrow.core.raise.catch
import arrow.core.right
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import com.tournament.core.domain.errors.ErrorType.INFRA_ERROR
import com.tournament.infra.adapter.input.repository.model.DbPlayer
import com.tournament.infra.adapter.output.service.errors.ApiError
import io.ktor.http.HttpStatusCode

private const val UNKNOWN = "Unknown"

private const val PLAYERS_TABLE = "players"

private const val PSEUDO_COL = "pseudo"

private const val POINTS_COL = "points"

private const val RANK_COL = "rank"

class DynamoPlayerRepository(private val client: DynamoDbClient) : PlayerDao {
    override suspend fun put(dbPlayer: DbPlayer): EitherNel<ApiError, String> =
        catch({
            putItem(dbPlayer)
            "Player ${dbPlayer.pseudo} successfully updated".right()
        }) {
            ApiError(
                httpCode = HttpStatusCode.BadRequest.value,
                reason = it.cause?.message ?: it.message.toString(),
                errorType = INFRA_ERROR
            ).leftNel()
        }

     suspend fun putItem(dbPlayer: DbPlayer) {
        client.putItem(PutItemRequest {
            val columnsValues = mapOf(
                PSEUDO_COL to AttributeValue.S(dbPlayer.pseudo),
                POINTS_COL to AttributeValue.S(dbPlayer.points.toString()),
                RANK_COL to (dbPlayer.rank?.let { AttributeValue.S(it) } ?: AttributeValue.S(UNKNOWN)),
            ).filterValues { it.asS() != UNKNOWN }
            tableName = PLAYERS_TABLE
            item = columnsValues

        })
    }

    override suspend fun delete(pseudo: String): EitherNel<ApiError, String> =
        catch({
            deleteItem(pseudo)
            "Player $pseudo successfully removed.".right()
        }) {
            ApiError(
                httpCode = HttpStatusCode.BadRequest.value,                
                reason = it.cause?.message ?: it.message.toString(),
                errorType = INFRA_ERROR
            ).leftNel()
        }

    suspend fun deleteItem(pseudo: String) {
        client.deleteItem(DeleteItemRequest {
            key = mapOf(PSEUDO_COL to AttributeValue.S(pseudo))
            tableName = PLAYERS_TABLE
        })
    }


    override suspend fun get(pseudo: String): Option<DbPlayer> {
        val response = client.getItem(GetItemRequest {
            key = mapOf(PSEUDO_COL to AttributeValue.S(pseudo))
            tableName = PLAYERS_TABLE
        })
        return fromNullable(response.item).map(::recordToValidPlayer)

    }

    override suspend fun all(): EitherNel<ApiError, List<DbPlayer>> =
        catch({
            val response = scanItem()
            val players = response.items?.map(::recordToValidPlayer)  ?: listOf()
            players.right()
        }) {
             ApiError(
                httpCode = HttpStatusCode.FailedDependency.value    ,
                 reason = it.cause?.message ?: it.message.toString(),
                 errorType = INFRA_ERROR
            ).leftNel()
        }

    suspend fun scanItem() = client.scan(ScanRequest { tableName = PLAYERS_TABLE })

    private fun recordToValidPlayer(record :Map<String, AttributeValue>) =
        DbPlayer(
            pseudo = record[PSEUDO_COL]!!.asS(),
            points = record[POINTS_COL]!!.asS().toInt(),
            rank = record[RANK_COL]?.asS()
        )


    suspend fun doesPseudoExist(pseudo: String): Boolean = get(pseudo).isSome()
}