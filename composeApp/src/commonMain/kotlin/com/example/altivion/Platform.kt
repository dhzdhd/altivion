package com.example.altivion

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform