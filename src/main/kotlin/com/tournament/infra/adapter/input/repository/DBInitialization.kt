package com.tournament.infra.adapter.input.repository

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemResponse
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType
import com.tournament.infra.plugins.PLAYER_TABLE_NAME
import com.tournament.infra.plugins.PSEUDO_ATTRIBUTE
import kotlinx.coroutines.CoroutineScope

val createTableRequest = CreateTableRequest {
    attributeDefinitions = listOf(
        AttributeDefinition {
            attributeName = PSEUDO_ATTRIBUTE
            attributeType = ScalarAttributeType.S
        }
    )
    keySchema = listOf(
        KeySchemaElement {
            attributeName = PSEUDO_ATTRIBUTE
            keyType = KeyType.Hash
        }
    )
    provisionedThroughput = ProvisionedThroughput {
        readCapacityUnits = 10
        writeCapacityUnits = 10
    }
    tableName = PLAYER_TABLE_NAME

}

fun initDatabase(
    dynamoDbClient: DynamoDbClient,
    createTableRequest: CreateTableRequest
): suspend CoroutineScope.() -> PutItemResponse = {
    val tableNames = dynamoDbClient.listTables().tableNames ?: listOf()
    if (!tableNames.contains(PLAYER_TABLE_NAME)) {
        dynamoDbClient.createTable(createTableRequest)
    }
    dynamoDbClient.putItem(PutItemRequest {
        tableName = PLAYER_TABLE_NAME
        item = mapOf(
            "pseudo" to AttributeValue.S("Bond"),
            "points" to AttributeValue.S("2000"),
            "rank" to AttributeValue.S("spy"),

            )
    })
    dynamoDbClient.putItem(PutItemRequest {
        tableName = PLAYER_TABLE_NAME
        item = mapOf(
            "pseudo" to AttributeValue.S("LeChiffre"),
            "points" to AttributeValue.S("2001"),
            "rank" to AttributeValue.S("Expert"),

            )
    })

}