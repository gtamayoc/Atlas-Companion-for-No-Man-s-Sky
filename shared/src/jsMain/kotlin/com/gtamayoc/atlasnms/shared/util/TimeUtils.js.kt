package com.gtamayoc.atlasnms.shared.util

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()
