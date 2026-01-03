package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import arrow.core.Option
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airplane
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
    val type: String? = null,
    val flight: String? = null,
    @SerialName("r") val registration: String? = null,
    @SerialName("t") val airframe: String? = null,
    val desc: String? = null,
    val lat: Double,
    val lon: Double,
    @SerialName("alt_baro") val altBaro: String? = null,
    @SerialName("alt_geom") val altGeom: Int? = null,
    @SerialName("gs") val groundSpeed: Double? = null,
    @SerialName("ias") val indicatedAirSpeed: Int? = null,
    @SerialName("tas") val trueAirSpeed: Int? = null,
    val mach: Double? = null,
    @SerialName("wd") val windDirection: Int? = null,
    @SerialName("ws") val windSpeed: Int? = null,
    @SerialName("oat") val outsideAirTemp: Int? = null,
    @SerialName("tat") val totalAirTemp: Int? = null,
    val track: Double? = null,
    @SerialName("track_rate") val trackRate: Double? = null,
    val roll: Double? = null,
    @SerialName("mag_heading") val magneticHeading: Double? = null,
    @SerialName("true_heading") val trueHeading: Double? = null,
    @SerialName("baro_rate") val baroRate: String? = null,
    @SerialName("geom_rate") val geomRate: Int? = null,
    val squawk: String? = null,
    val emergency: String? = null,
    val category: String? = null,
    @SerialName("nav_qnh") val navQnh: Double? = null,
    @SerialName("nav_altitude_mcp") val navAltitudeMcp: Int? = null,
    @SerialName("nav_heading") val navHeading: Double? = null,
    @SerialName("nav_modes") val navModes: List<String> = listOf(),
    @SerialName("nic") val navIntegrityCategory: Int? = null,
    @SerialName("rc") val containmentRadius: Int? = null,
    @SerialName("nic_baro") val baroAltIntegrity: Int? = null,
    @SerialName("nac_p") val navAccuracyPosition: Int? = null,
    @SerialName("nac_v") val navAccuracyVelocity: Int? = null,
    @SerialName("sil") val sourceIntegrityLevel: Int? = null,
    @SerialName("sil_type") val silType: String? = null,
    @SerialName("gva") val geometricVerticalAccuracy: Int? = null,
    @SerialName("sda") val systemDesignAssurance: Int? = null,
    @SerialName("alert") val alertFlag: Int? = null,
    @SerialName("spi") val specialPurposeIndicator: Int? = null,
    @SerialName("version") val adsbVersion: Int? = null,
    @SerialName("seen") val timeSinceLastMsg: Double? = null,
    @SerialName("seen_pos") val timeSinceLastPos: Double? = null,
    @SerialName("messages") val totalMessages: Int? = null,
    @SerialName("rssi") val signalStrength: Double? = null,
    @SerialName("mlat") val multilaterationData: List<String> = listOf(),
    @SerialName("tisb") val trafficInfoServiceBroadcast: List<String> = listOf(),
    @SerialName("dst") val receiverDist: Double? = null,
    @SerialName("dir") val receiverDir: Double? = null,
) {
  fun toAirplane(): Airplane =
      Airplane(
          hex = hex,
          type = Option.fromNullable(type),
          flight = Option.fromNullable(flight),
          registration = Option.fromNullable(registration),
          airframe = Option.fromNullable(airframe),
          description = Option.fromNullable(desc),
          latitude = lat,
          longitude = lon,
          barometricAltitude = Option.fromNullable(altBaro),
          geometricAltitude = Option.fromNullable(altGeom),
          groundSpeed = Option.fromNullable(groundSpeed),
          indicatedAirSpeed = Option.fromNullable(indicatedAirSpeed),
          trueAirSpeed = Option.fromNullable(trueAirSpeed),
          mach = Option.fromNullable(mach),
          windDirection = Option.fromNullable(windDirection),
          windSpeed = Option.fromNullable(windSpeed),
          outsideAirTemperature = Option.fromNullable(outsideAirTemp),
          totalAirTemperature = Option.fromNullable(totalAirTemp),
          track = Option.fromNullable(track),
          trackRate = Option.fromNullable(trackRate),
          roll = Option.fromNullable(roll),
          magneticHeading = Option.fromNullable(magneticHeading),
          trueHeading = Option.fromNullable(trueHeading),
          barometricRate = Option.fromNullable(baroRate),
          geometricRate = Option.fromNullable(geomRate),
          squawk = Option.fromNullable(squawk),
          emergency = Option.fromNullable(emergency),
          category = Option.fromNullable(category),
          navigationQnh = Option.fromNullable(navQnh),
          navigationAltitudeMcp = Option.fromNullable(navAltitudeMcp),
          navigationHeading = Option.fromNullable(navHeading),
          navigationModes = navModes,
          navigationIntegrityCategory = Option.fromNullable(navIntegrityCategory),
          radiusOfContainment = Option.fromNullable(containmentRadius),
          barometricAltitudeIntegrity = Option.fromNullable(baroAltIntegrity),
          navigationAccuracyPosition = Option.fromNullable(navAccuracyPosition),
          navigationAccuracyVelocity = Option.fromNullable(navAccuracyVelocity),
          sourceIntegrityLevel = Option.fromNullable(sourceIntegrityLevel),
          sourceIntegrityLevelType = Option.fromNullable(silType),
          geometricVerticalAccuracy = Option.fromNullable(geometricVerticalAccuracy),
          systemDesignAssurance = Option.fromNullable(systemDesignAssurance),
          alertFlag = Option.fromNullable(alertFlag),
          specialPurposeIndicator = Option.fromNullable(specialPurposeIndicator),
          adsbVersion = Option.fromNullable(adsbVersion),
          timeSinceLastMessage = Option.fromNullable(timeSinceLastMsg),
          timeSinceLastPosition = Option.fromNullable(timeSinceLastPos),
          totalMessages = Option.fromNullable(totalMessages),
          signalStrength = Option.fromNullable(signalStrength),
          multilateration = multilaterationData,
          trafficInfoServiceBroadcast = trafficInfoServiceBroadcast,
          receiverDistance = Option.fromNullable(receiverDist),
          receiverDirection = Option.fromNullable(receiverDir))
}

interface AirplaneAPI {
  suspend fun getAirplanesByLatLon(
      lat: Double,
      lon: Double,
      radius: Double
  ): Either<AppError, AirplanesLiveRouteDTO>
}

@Single
class AirplanesLiveAPI(private val httpClient: HttpClient) : AirplaneAPI {
  override suspend fun getAirplanesByLatLon(
      lat: Double,
      lon: Double,
      radius: Double
  ): Either<AppError, AirplanesLiveRouteDTO> {
    return Either.catch {
          val resp =
              httpClient
                  .get {
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
                  }
                  .body<AirplanesLiveRouteDTO>()

          return Either.Right(resp)
        }
        .mapLeft { AppError.NetworkError("Failed to fetch airplane info from airplanes.live", it) }
  }
}
