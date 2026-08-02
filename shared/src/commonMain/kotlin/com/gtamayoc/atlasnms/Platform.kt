package com.gtamayoc.atlasnms

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform