package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.repositories.AirplaneDTO
import dev.dhzdhd.altivion.home.repositories.AirplanesLiveRouteAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<AppError, Location>

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
                    it.aircraft.map(AirplaneDTO::toAirplane)
                })
                delay(2000)
            }
        }
    }
}
