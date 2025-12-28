package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import arrow.core.Option
import arrow.core.toOption
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.repositories.AirplanesLiveRouteAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.koin.core.annotation.Single

data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<AppError, Location>

typealias SerializableOption<T> = @Serializable(with = OptionSerializer::class) Option<T>

@Serializable
data class Airplane(
    val longitude: Double,
    val latitude: Double,
    val id: String,
    val airframe: SerializableOption<String>,
    val identifier: SerializableOption<String>,
    val groundSpeed: SerializableOption<Double>
)

class OptionSerializer<T>(private val dataSerializer: KSerializer<T?>) : KSerializer<Option<T?>> {
    override fun serialize(encoder: Encoder, value: Option<T?>) =
        dataSerializer.serialize(encoder, value.getOrNull())

    override fun deserialize(decoder: Decoder): Option<T> =
        Option.fromNullable(dataSerializer.deserialize(decoder))

    override val descriptor: SerialDescriptor = SerialDescriptor(
        "my.app.Option",
        dataSerializer.descriptor
    )
}

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
