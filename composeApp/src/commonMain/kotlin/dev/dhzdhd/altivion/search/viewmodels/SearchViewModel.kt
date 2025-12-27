package dev.dhzdhd.altivion.search.viewmodels

import androidx.lifecycle.ViewModel
import dev.dhzdhd.altivion.common.Action
import dev.dhzdhd.altivion.common.Store
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.android.annotation.KoinViewModel

sealed interface SearchAction: Action {
}

@KoinViewModel
class SearchViewModel(): ViewModel(), Store<SearchAction> {
    private val _snackBarEvents = MutableSharedFlow<String>()
    val snackBarEvents = _snackBarEvents.asSharedFlow()

    override fun dispatch(action: SearchAction) {
    }
}