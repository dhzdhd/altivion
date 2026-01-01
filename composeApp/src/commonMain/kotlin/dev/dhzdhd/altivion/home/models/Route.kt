@file:UseSerializers(OptionSerializer::class)

package dev.dhzdhd.altivion.home.models

import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class RouteAndAirline(
    val route: Route,
    val airline: Airline
)

@Serializable
data class Route(
    val origin: Airport,
    val destination: Airport
)

@Serializable
data class Airline(
    val name: String,
    val icao: String,
    val iata: Option<String>,
    val country: String,
    @SerialName("country_iso") val countryISO: String,
    val callsign: Option<String>,
)

@Serializable
data class Airport(
    val countryISOName: String,
    val countryName: String,
    val elevation: Double,
    val iataCode: String,
    val icaoCode: String,
    val latitude: Double,
    val longitude: Double,
    val municipality: String,
    val name: String,
)