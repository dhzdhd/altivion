package dev.dhzdhd.altivion.common

interface Action

interface Store<in T: Action> {
    fun dispatch(action: T)
}
