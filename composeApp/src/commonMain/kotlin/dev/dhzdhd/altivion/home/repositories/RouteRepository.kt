package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.koin.core.annotation.Single

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AirplanesLiveRouteDTO(
    @SerialName("ac") val aircraft: List<AirplaneDTO>,
    val total: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AirplaneDTO(
    val hex: String,
    val flight: String? = null,
    val desc: String? = null,
    @SerialName("r") val ident: String? = null,
    @SerialName("t") val airframe: String? = null,
    @SerialName("gs") val groundSpeed: Double? = null,
    val squawk: Double? = null,
    val track: Double? = null,
    val lat: Double,
    val lon: Double,
)

interface RouteAPI {
    suspend fun getAirplanesByLatLon(
        lat: Double, lon: Double, radius: Double
    ): Either<AppError, AirplanesLiveRouteDTO>
}

@Single
class AirplanesLiveRouteAPI(private val httpClient: HttpClient): RouteAPI {
    override suspend fun getAirplanesByLatLon(
        lat: Double, lon: Double, radius: Double
    ): Either<AppError, AirplanesLiveRouteDTO> {
        return Either.catch {
            val resp = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.airplanes.live"
                    appendPathSegments(
                        "v2",
                        "point",
                        lat.toString(),
                        lon.toString(),
                        radius.toString(),
                    )
                }
            }.body<AirplanesLiveRouteDTO>()

            return Either.Right(resp)

        }.mapLeft { AppError.NetworkError("Failed to fetch airplane info from airplanes.live", it) }
    }
}
