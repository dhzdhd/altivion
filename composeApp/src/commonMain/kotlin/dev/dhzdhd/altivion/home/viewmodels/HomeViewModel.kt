package dev.dhzdhd.altivion.home.viewmodels

import androidx.lifecycle.ViewModel
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Store
import dev.dhzdhd.altivion.home.models.RouteDTO
import dev.dhzdhd.altivion.home.repositories.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel
import kotlin.collections.listOf

data class HomeState(val currentLocation: Int) {
}

sealed interface HomeAction: Action {
    object GetAllItems: HomeAction
}

@KoinViewModel
class HomeViewModel(private val repo: HomeRepository): ViewModel(), Store<HomeAction> {
    private val state = MutableStateFlow<List<RouteDTO>>(listOf())
    val items = state.asStateFlow()

    override fun dispatch(action: HomeAction) {
        when (action) {
            is HomeAction.GetAllItems -> println(repo.get())
        }
    }
}