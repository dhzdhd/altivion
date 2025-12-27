package dev.dhzdhd.altivion

import dev.dhzdhd.altivion.home.HomeModule
import dev.dhzdhd.altivion.search.SearchModule
import dev.dhzdhd.altivion.settings.SettingsModule
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.ksp.generated.startKoin
import org.koin.mp.KoinPlatform

@Configuration
@Module(includes = [HomeModule::class, SearchModule::class, SettingsModule::class])
class AppModule

@KoinApplication
object KoinApp

fun initKoin(configuration: KoinAppDeclaration? = null) {
    KoinApp.startKoin {
        includes(configuration)
    }
}

