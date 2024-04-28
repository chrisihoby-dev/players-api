package com.tournament.infra.plugins

import com.tournament.infra.dtos.PlayerDto
import com.tournament.infra.dtos.PlayerUpdateDto
import com.tournament.infra.extensions.receiveWithExceptionHandling
import com.tournament.infra.adapter.output.service.PlayerService
import com.tournament.infra.plugins.docapi.deletePlayerDoc
import com.tournament.infra.plugins.docapi.getAllPlayerDoc
import com.tournament.infra.plugins.docapi.getPlayerDoc
import com.tournament.infra.plugins.docapi.postPlayerDoc
import com.tournament.infra.plugins.docapi.putPlayerDoc
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.github.smiley4.ktorswaggerui.dsl.delete

import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    routing {
        val playerService by inject<PlayerService>()
        post("/player",OpenApiRoute::postPlayerDoc) {
            call.receiveWithExceptionHandling<PlayerDto>()?.let {
                playerService.addPlayer(it)
                .onLeft { inputErrors -> call.respond(status = BadRequest, message = inputErrors) }
                .onRight { response -> call.respondText(status = Created, text = response) }
            }
        }

        put("/player/{pseudo}", OpenApiRoute::putPlayerDoc) {
            call.receiveWithExceptionHandling<PlayerUpdateDto>()?.let {
                playerService.updatePlayer(
                    pseudo = call.parameters["pseudo"],
                    playerDto = it,
                    forceCreate = call.request.queryParameters["forceCreate"]?.toBoolean() ?: false
                )
                .onLeft { inputErrors -> call.respond(status = BadRequest, message = inputErrors) }
                .onRight { response -> call.respondText(status = Created, text = response) }
            }
        }
        get("/player/{pseudo}",OpenApiRoute::getPlayerDoc) {
            call.parameters["pseudo"]?.let { pseudo ->
                playerService.getPlayer(pseudo)
                    .onLeft { inputErrors ->
                        val httpCode = inputErrors.first().httpCode?.let { HttpStatusCode.fromValue(it) } ?: BadRequest
                        call.respond(
                            status = httpCode,
                            message = inputErrors
                        )
                    }.onRight {
                        call.respond(status = OK, message = it)
                    }
            }
        }
        get("/player/all",OpenApiRoute::getAllPlayerDoc) {
            playerService.getAllPlayers(call.request.queryParameters["sortBy"])
                .onLeft { inputErrors ->
                    call.respond(
                        status = inputErrors.first().httpCode?.let { HttpStatusCode.fromValue(it) } ?: BadRequest,
                        message = inputErrors
                    )
                }.onRight {
                    call.respond(status = OK, message = it)
                }

        }
        delete("/player/{pseudo}", OpenApiRoute::deletePlayerDoc) {
            call.parameters["pseudo"]?.let { pseudo ->
                playerService.deletePlayer(pseudo)
                    .onLeft { inputErrors ->
                        call.respond(
                            status = inputErrors.first().httpCode?.let { HttpStatusCode.fromValue(it) } ?: BadRequest,
                            message = inputErrors
                        )
                    }.onRight {
                        call.respond(status = OK, message = it)
                    }
            }
        }
    }
}
