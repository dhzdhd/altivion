package dev.dhzdhd.altivion

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
