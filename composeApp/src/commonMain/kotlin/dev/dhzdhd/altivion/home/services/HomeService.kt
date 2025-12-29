package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.repositories.AirplaneDTO
import dev.dhzdhd.altivion.home.repositories.AirplaneImage
import dev.dhzdhd.altivion.home.repositories.AirplanesLiveRouteAPI
import dev.dhzdhd.altivion.home.repositories.ImageAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<AppError, Location>

@Single
class HomeService(private val routeApi: AirplanesLiveRouteAPI, private val imageApi: ImageAPI) {
    fun getAirplanes(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): Flow<Either<AppError, List<Airplane>>> {
        return flow {
            while (true) {
                println("Fetching airplane data")
                emit(routeApi.getAirplanesByLatLon(latitude, longitude, radius).map {
                    it.aircraft.map(AirplaneDTO::toAirplane)
                })
                delay(2000)
            }
        }
    }

    suspend fun getAirplaneImage(airplane: Airplane): Either<AppError, AirplaneImage> =
        imageApi.getImage(airplane)
}