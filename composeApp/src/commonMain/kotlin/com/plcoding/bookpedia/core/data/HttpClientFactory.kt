package com.plcoding.bookpedia.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// factory is a class that is responsible for creating complex objects
// each kind of platform passes to it an engine for it to create an http client
object HttpClientFactory {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            // install plugin to serve different purpose

            // to parse JSON into our data class
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true // ignore keys that we don't specify in our serializable instead of crashing our app
                    }
                )
            }

            install(HttpTimeout) {
                // timeout after 20s
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }

            install(Logging) {
                // log requests for debugging
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }

            defaultRequest { // what we want to configure for every request in our application
                contentType(ContentType.Application.Json) // every request is working with Json data and we specify that here
                // so the the client knows that it can expect json data
            }

        }
    }
}