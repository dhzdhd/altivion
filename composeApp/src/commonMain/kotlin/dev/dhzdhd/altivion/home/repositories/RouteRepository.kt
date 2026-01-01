package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airline
import dev.dhzdhd.altivion.home.models.Airport
import dev.dhzdhd.altivion.home.models.Route
import dev.dhzdhd.altivion.home.models.RouteAndAirline
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.koin.core.annotation.Single

@Serializable
data class ADSBDBDTO(
    val response: ADSBDBResponseDTO,
) {
    fun toRouteAndAirline(): RouteAndAirline {
        val originDTO = response.flightRoute.origin
        val origin = Airport(
            countryISOName = originDTO.countryISOName,
            countryName = originDTO.countryName,
            elevation = originDTO.elevation,
            iataCode = originDTO.iataCode,
            icaoCode = originDTO.icaoCode,
            latitude = originDTO.latitude,
            longitude = originDTO.longitude,
            municipality = originDTO.municipality,
            name = originDTO.name,
        )

        val destinationDTO = response.flightRoute.destination
        val destination = Airport(
            countryISOName = destinationDTO.countryISOName,
            countryName = destinationDTO.countryName,
            elevation = destinationDTO.elevation,
            iataCode = destinationDTO.iataCode,
            icaoCode = destinationDTO.icaoCode,
            latitude = destinationDTO.latitude,
            longitude = destinationDTO.longitude,
            municipality = destinationDTO.municipality,
            name = destinationDTO.name,
        )

        val airlineDTO = response.flightRoute.airline
        val airline = Airline(
            name = airlineDTO.name,
            icao = airlineDTO.icao,
            iata = Option.fromNullable(airlineDTO.iata),
            country = airlineDTO.country,
            countryISO = airlineDTO.countryISO,
            callsign = Option.fromNullable(airlineDTO.callsign),
        )

        return RouteAndAirline(
            route = Route(
                origin = origin,
                destination = destination
            ),
            airline = airline
        )
    }
}

@Serializable
data class ADSBDBResponseDTO(
    @SerialName("flightroute") val flightRoute: ADSBDBFlightRouteDTO,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ADSBDBFlightRouteDTO(
    val callsign: String,
    @SerialName("callsign_icao") val callsignICAO: String?,
    @SerialName("callsign_iata") val callsignIATA: String?,
    val airline: ADSBDBAirlineDTO,
    val origin: ADSBDBAirportDTO,
    val destination: ADSBDBAirportDTO
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ADSBDBAirlineDTO(
    val name: String,
    val icao: String,
    val iata: String?,
    val country: String,
    @SerialName("country_iso") val countryISO: String,
    val callsign: String?,
)

@Serializable
data class ADSBDBAirportDTO(
    @SerialName("country_iso_name") val countryISOName: String,
    @SerialName("country_name") val countryName: String,
    val elevation: Double,
    @SerialName("iata_code") val iataCode: String,
    @SerialName("icao_code") val icaoCode: String,
    val latitude: Double,
    val longitude: Double,
    val municipality: String,
    val name: String,
)

interface RouteAPI {
    suspend fun getRouteAndAirlineByAirplaneCallsign(callsign: Option<String>): Either<AppError, ADSBDBDTO>
}

@Single
class ADSBDBRouteApi(val httpClient: HttpClient) : RouteAPI {
    override suspend fun getRouteAndAirlineByAirplaneCallsign(callsign: Option<String>): Either<AppError, ADSBDBDTO> {
        return Either.catch {
            val resp = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.adsbdb.com"
                    appendPathSegments(
                        "v0",
                        "callsign",
                        callsign.getOrElse { "" },
                    )
                }
            }.body<ADSBDBDTO>()

            resp
        }.mapLeft {
            AppError.NetworkError(
                "Failed to fetch airline and route info from adsbdb.com",
                it
            )
        }
    }
}