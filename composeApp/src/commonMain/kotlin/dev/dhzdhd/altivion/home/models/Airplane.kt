
@file:UseSerializers(OptionSerializer::class)

package dev.dhzdhd.altivion.home.models

import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Airplane(
    val hex: String,
    val type: Option<String>,
    val flight: Option<String>,
    val registration: Option<String>,
    val airframe: Option<String>,
    val description: Option<String>,
    val latitude: Double,
    val longitude: Double,
    val barometricAltitude: Option<String>,
    val geometricAltitude: Option<Int>,
    val groundSpeed: Option<Double>,
    val indicatedAirSpeed: Option<Int>,
    val trueAirSpeed: Option<Int>,
    val mach: Option<Double>,
    val windDirection: Option<Int>,
    val windSpeed: Option<Int>,
    val outsideAirTemperature: Option<Int>,
    val totalAirTemperature: Option<Int>,
    val track: Option<Double>,
    val trackRate: Option<Double>,
    val roll: Option<Double>,
    val magneticHeading: Option<Double>,
    val trueHeading: Option<Double>,
    val barometricRate: Option<String>,
    val geometricRate: Option<Int>,
    val squawk: Option<String>,
    val emergency: Option<String>,
    val category: Option<String>,
    val navigationQnh: Option<Double>,
    val navigationAltitudeMcp: Option<Int>,
    val navigationHeading: Option<Double>,
    val navigationModes: List<String>,
    val navigationIntegrityCategory: Option<Int>,
    val radiusOfContainment: Option<Int>,
    val barometricAltitudeIntegrity: Option<Int>,
    val navigationAccuracyPosition: Option<Int>,
    val navigationAccuracyVelocity: Option<Int>,
    val sourceIntegrityLevel: Option<Int>,
    val sourceIntegrityLevelType: Option<String>,
    val geometricVerticalAccuracy: Option<Int>,
    val systemDesignAssurance: Option<Int>,
    val alertFlag: Option<Int>,
    val specialPurposeIndicator: Option<Int>,
    val adsbVersion: Option<Int>,
    val timeSinceLastMessage: Option<Double>,
    val timeSinceLastPosition: Option<Double>,
    val totalMessages: Option<Int>,
    val signalStrength: Option<Double>,
    val multilateration: List<String>,
    val trafficInfoServiceBroadcast: List<String>,
    val receiverDistance: Option<Double>,
    val receiverDirection: Option<Double>,
)
