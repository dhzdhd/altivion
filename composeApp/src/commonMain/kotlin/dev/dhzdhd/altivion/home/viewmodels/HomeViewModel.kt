package dev.dhzdhd.altivion.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import co.touchlab.kermit.Logger
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Log
import dev.dhzdhd.altivion.common.Store
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.services.HomeService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.spatialk.geojson.Position

data class HomeState(val position: Position, val zoom: Double) {
    companion object {
        val Default = HomeState(Position(0.0, 0.0), 0.0)
    }
}

sealed interface HomeAction : Action {
    data object GetAllItems : HomeAction
    data class ShowSnackBar(val message: String) : HomeAction
    data class UpdateCameraState(val position: Position, val zoom: Double) : HomeAction
}

@KoinViewModel
class HomeViewModel(private val service: HomeService, private val logger: Logger) : ViewModel(), Store<HomeAction> {
    private val _logger = logger.withTag("HomeViewModel")

    private val _airplanesState = MutableStateFlow<Value<List<Airplane>>>(Value.Loading)
    val airplanes = _airplanesState.asStateFlow()

    private val _snackBarEvents = MutableSharedFlow<String>()
    val snackBarEvents = _snackBarEvents.asSharedFlow()

    private val _state = MutableStateFlow(HomeState.Default)
    val state = _state.asStateFlow()

    private var fetchJob: Job? = null

    init {
        startFetching()
    }

    private fun startFetching() {
        fetchJob?.cancel()
        fetchJob = service.getAirplanes(
            state.value.position.latitude,
            state.value.position.longitude,
            calculateRadius(state.value.zoom)
        ).onEach { result ->
                _airplanesState.value = when (result) {
                    is Either.Left -> Value.Error(result.value)
                    is Either.Right -> Value.Data(result.value)
                }
                println(_airplanesState.value)
            }.launchIn(viewModelScope)
    }

    private fun stopFetching() {
        fetchJob?.cancel()
        fetchJob = null
    }

    private fun restartFetching() {
        stopFetching()
        startFetching()
    }

    override fun dispatch(action: HomeAction) {
        when (action) {
            is HomeAction.GetAllItems -> println("")
            is HomeAction.ShowSnackBar -> showSnackBar(action.message)
            is HomeAction.UpdateCameraState -> updateCameraState(action.position, action.zoom)
        }
    }

    @Log private fun updateCameraState(position: Position, zoom: Double) {
        _logger.i { "Entered updateCameraState with position: $position and zoom: $zoom" }
        _state.value = _state.value.copy(position = position, zoom = zoom)
        restartFetching()
    }

    private fun showSnackBar(message: String) {
        viewModelScope.launch {
            _snackBarEvents.emit(message)
        }
    }

    private fun calculateRadius(zoom: Double): Double {
        val clampedZoom = zoom.coerceIn(2.0, 15.0)
        return 2500.0 / clampedZoom
    }

    override fun onCleared() {
        super.onCleared()
        stopFetching()
    }
}