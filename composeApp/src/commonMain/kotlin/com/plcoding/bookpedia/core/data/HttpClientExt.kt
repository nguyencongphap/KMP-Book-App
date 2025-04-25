package com.plcoding.bookpedia.core.data

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.DataError.Remote
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext


// handy util function that safely executes an HTTP call
// and catches the necessary Exceptions there
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute() // execute the request
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) { // as long as server is live, this error means no internet connection
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception) {
        // when catching general exception, we might accidentally catch cancellation exceptions
        // thrown by suspend function when a coroutine is canceled in kotlin
        // and therefore breaks the default mechanism of scope and coroutine.
        // The parent scope might not be properly notified and that can lead to issues
        coroutineContext.ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return responseToResult(response)
}

// this is a util function that takes in an http response from an API
// and tries to extract JSON body from that response and parse it into type T that we expect
// "reified" keyword is needed because we need to use the type info of the generic argument in the function definition
suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Remote> {
    return when(response.status.value) {
        in 200 .. 299 -> { // successful request
            // try parsing json body to type T
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500 .. 599 -> Result.Error(DataError.Remote.SERVER) // server error
        else -> Result.Error(DataError.Remote.UNKNOWN)

    }
}