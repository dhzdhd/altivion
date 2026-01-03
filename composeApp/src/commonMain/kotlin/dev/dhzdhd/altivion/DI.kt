package dev.dhzdhd.altivion

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import dev.dhzdhd.altivion.home.HomeModule
import dev.dhzdhd.altivion.search.SearchModule
import dev.dhzdhd.altivion.settings.SettingsModule
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.ksp.generated.startKoin

@Configuration
@Module(includes = [HomeModule::class, SearchModule::class, SettingsModule::class])
class AppModule {
  @Factory fun provideLogger(): Logger = Logger
}

@KoinApplication object KoinApp

fun initKoin(configuration: KoinAppDeclaration? = null) {
  KoinApp.startKoin {
    logger(KermitKoinLogger(Logger.withTag("Koin")))
    includes(configuration)
    configuration?.invoke(this)
  }
}
