@file:UseSerializers(OptionSerializer::class)

package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import arrow.core.toOption
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.repositories.AirplanesLiveRouteAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.koin.core.annotation.Single

data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<AppError, Location>

@Serializable
data class Airplane(
    val longitude: Double,
    val latitude: Double,
    val id: String,
    val airframe: Option<String>,
    val identifier: Option<String>,
    val groundSpeed: Option<Double>
)

@Single
class HomeService(private val api: AirplanesLiveRouteAPI) {
    fun getAirplanes(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): Flow<Either<AppError, List<Airplane>>> {
        return flow {
            while (true) {
                println("Fetching airplane data")
                emit(api.getAirplanesByLatLon(latitude, longitude, radius).map {
                    it.aircraft.map { airplane ->
                        Airplane(
                            airplane.lon,
                            airplane.lat,
                            airplane.hex,
                            airplane.airframe.toOption(),
                            airplane.ident.toOption(),
                            airplane.groundSpeed.toOption()
                        )
                    }
                })
                delay(10000)
            }
        }
    }
}
