package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Single

@Single
class HomeRepository(private val httpClient: HttpClient) {
  suspend fun getRoutes(): Either<Throwable, String> {
    return Either.catch { httpClient.get("") {}.body() }
  }
}
