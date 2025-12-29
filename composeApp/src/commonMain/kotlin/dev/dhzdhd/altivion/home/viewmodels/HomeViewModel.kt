package dev.dhzdhd.altivion.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Store
import dev.dhzdhd.altivion.common.Value
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.services.HomeService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface HomeAction: Action {
    data object GetAllItems: HomeAction
    data class ShowSnackBar(val message: String): HomeAction
}

@KoinViewModel
class HomeViewModel(private val service: HomeService): ViewModel(), Store<HomeAction> {
    private val state = MutableStateFlow<Value<List<Airplane>>>(Value.Loading)
    val airplanes = state.asStateFlow()

    private val _snackBarEvents = MutableSharedFlow<String>()
    val snackBarEvents = _snackBarEvents.asSharedFlow()

    init {
        service.getAirplanes(17.3753, 78.4744, 500.0)
            .onEach { result ->
                state.value = when(result) {
                    is Either.Left -> Value.Error(result.value)
                    is Either.Right -> Value.Data(result.value)
                }
                println(state.value)
            }
            .launchIn(viewModelScope)
    }

    override fun dispatch(action: HomeAction) {
        when (action) {
            is HomeAction.GetAllItems -> println("")
            is HomeAction.ShowSnackBar -> showSnackBar(action.message)
        }
    }

    fun showSnackBar(message: String) {
        viewModelScope.launch {
            _snackBarEvents.emit(message)
        }
    }
}