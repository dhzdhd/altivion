package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
private data class IpLocation(val latitude: Double, val longitude: Double)

actual fun getLocation(): Either<AppError, Location> {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        osName.contains("win") -> {
            getLocationFromIp()
        }
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> {
            getLocationFromIp()
        }
        osName.contains("mac") -> {
            getLocationFromIp()
        }
        else -> {
            getLocationFromIp()
        }
    }
}

private fun getLocationFromIp(): Either<AppError, Location> = runBlocking {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    Either.catch {
        val response: IpLocation = client.get("https://ipapi.co/json").body()
        client.close()

        Location(response.latitude, response.longitude)
    }.mapLeft {
        client.close()
        AppError.NetworkError(
            message = it.message ?: "",
            error = it
        )
    }
}
