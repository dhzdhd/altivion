package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import arrow.core.getOrElse
import coil3.Uri
import coil3.pathSegments
import coil3.toUri
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airplane
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import okio.Path
import org.koin.core.annotation.Single

@Serializable
data class AirplaneImageDTO(val status: Int, val count: Int, val data: List<AirplaneImage>)

@Serializable
data class AirplaneImage(val image: String, val link: String, val photographer: String)

@Single
class ImageAPI(private val httpClient: HttpClient) {
    suspend fun getImage(airplane: Airplane): Either<AppError, AirplaneImage> {
        return Either.catch {
            val resp = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "airport-data.com"
                    appendPathSegments(
                        "api",
                        "ac_thumb.json"
                    )
                    parameters.append("m", airplane.hex.uppercase())
                    parameters.append("n", "1")
                    parameters.append("r", airplane.registration.getOrElse { "" }.trim().uppercase())
                }
            }.body<AirplaneImageDTO>()

            val data = resp.data.map {
                val imageId = it.link.toUri().pathSegments.last().removePrefix("000").removeSuffix(".html")
                val imageLink = "https://image.airport-data.com/aircraft/$imageId.jpg"
                it.copy(image=imageLink)
            }
            return Either.Right(data.first())
        }.mapLeft { AppError.NetworkError("Failed to fetch airplane image", it) }
    }
}