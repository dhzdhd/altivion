package dev.dhzdhd.altivion.settings.viewmodels

import altivion.composeapp.generated.resources.Res
import androidx.lifecycle.ViewModel
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Store
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

enum class ThemeMode(val displayName: String) {
    Dark("Dark"),
    Light("Light")
}

enum class MapStyle(val displayName: String, val url: String) {
    ProtomapsLight("Protomaps Light", Res.getUri("files/mapstyles/proto_light.json")),
    ProtomapsDark("Protomaps Dark", Res.getUri("files/mapstyles/proto_dark.json")),
    ProtomapsDarkWhite("Protomaps White", Res.getUri("files/mapstyles/proto_dark_white.json")),
    ProtomapsDarkGreyscale("Protomaps Greyscale", Res.getUri("files/mapstyles/proto_dark_greyscale.json")),
    ProtomapsDarkBlack("Protomaps Dark Black", Res.getUri("files/mapstyles/proto_dark_black.json"))
}

data class SettingsState(
    val themeMode: ThemeMode,
    val lightMapStyle: MapStyle,
    val darkMapStyle: MapStyle
) {
    companion object {
        val DEFAULT =
            SettingsState(ThemeMode.Dark, MapStyle.ProtomapsLight, MapStyle.ProtomapsDark)
    }
}

sealed interface SettingsAction : Action {
    data class UpdateTheme(val themeMode: ThemeMode) : SettingsAction
    data class UpdateLightMapStyle(val mapStyle: MapStyle) : SettingsAction
    data class UpdateDarkMapStyle(val mapStyle: MapStyle) : SettingsAction
}

@KoinViewModel
class SettingsViewModel() : ViewModel(), Store<SettingsAction> {
    private val _settings = MutableStateFlow(SettingsState.DEFAULT)
    val settings = _settings.asStateFlow()

    private val _snackBarEvents = MutableSharedFlow<String>()
    val snackBarEvents = _snackBarEvents.asSharedFlow()

    override fun dispatch(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateTheme -> updateTheme(action.themeMode)
            is SettingsAction.UpdateLightMapStyle -> updateLightMapStyle(action.mapStyle)
            is SettingsAction.UpdateDarkMapStyle -> updateDarkMapStyle(action.mapStyle)
        }
    }

    private fun updateTheme(themeMode: ThemeMode) {
        _settings.value = _settings.value.copy(themeMode = themeMode)
    }

    private fun updateLightMapStyle(mapStyle: MapStyle) {
        _settings.value = _settings.value.copy(lightMapStyle = mapStyle)
    }

    private fun updateDarkMapStyle(mapStyle: MapStyle) {
        _settings.value = _settings.value.copy(darkMapStyle = mapStyle)
    }
}