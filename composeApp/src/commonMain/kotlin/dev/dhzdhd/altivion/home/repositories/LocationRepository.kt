package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.services.Location
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single


@Serializable
private data class IpLocation(val latitude: Double, val longitude: Double)


interface LocationRepository {
    suspend fun getLocation(): Either<AppError, Location>
}

@Single
expect class PlatformLocationRepository : LocationRepository {
    override suspend fun getLocation(): Either<AppError, Location>
}

@Single
class WebLocationRepository(private val client: HttpClient) : LocationRepository {
    override suspend fun getLocation(): Either<AppError, Location> {
        return Either.catch {
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
}

