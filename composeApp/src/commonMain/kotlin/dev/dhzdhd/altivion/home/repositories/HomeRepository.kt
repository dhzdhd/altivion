package dev.dhzdhd.altivion.home.repositories

import dev.dhzdhd.altivion.home.models.RouteDTO
import org.koin.core.annotation.Single

@Single
class HomeRepository {
    fun get(): RouteDTO {
        return RouteDTO(a = "")
    }
}