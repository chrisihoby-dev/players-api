package com.tournament

import com.tournament.infra.plugins.configureContext
import com.tournament.infra.plugins.configureRouting
import com.tournament.infra.plugins.configureSerialization

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureContext()
    configureSerialization()
    configureRouting()
}
