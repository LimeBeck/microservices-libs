package dev.limebeck.libs.testUtils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise

val testScope = MainScope()
actual fun runTest(block: suspend () -> Unit): Unit {
    testScope.promise { block() }
}