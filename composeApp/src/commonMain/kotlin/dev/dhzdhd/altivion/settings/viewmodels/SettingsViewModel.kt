package dev.dhzdhd.altivion.settings.viewmodels

import androidx.lifecycle.ViewModel
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Store
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

enum class ThemeMode(val displayName: String) {
    DARK("Dark"),
    LIGHT("Light")
}

data class SettingsState(val themeMode: ThemeMode) {
    companion object {
        val DEFAULT = SettingsState(ThemeMode.DARK)
    }
}

sealed interface SettingsAction : Action {
    data class UpdateTheme(val themeMode: ThemeMode) : SettingsAction
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
        }
    }

    private fun updateTheme(themeMode: ThemeMode) {
        _settings.value = _settings.value.copy(themeMode = themeMode)
    }
}