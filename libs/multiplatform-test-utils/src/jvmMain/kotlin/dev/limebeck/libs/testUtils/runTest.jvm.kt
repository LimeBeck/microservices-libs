package dev.limebeck.libs.testUtils

import kotlinx.coroutines.runBlocking

actual fun runTest(block: suspend () -> Unit): Unit = runBlocking { block() }