package dev.limebeck.libs.provider

import dev.limebeck.libs.testUtils.awaitAssertWithDelay
import dev.limebeck.libs.testUtils.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProviderTests {
    @Test
    fun workWithProviders() = runTest {
        val provider = providerOf { 1 }
        val mappedProvider = provider.map { it + 100 }
        val mappedOfMappedProvider = mappedProvider.map { it.toString() }

        val smth = Smth(provider)

        assertEquals(1, provider.get())
        assertEquals(1, smth.value)
        assertEquals(101, mappedProvider.get())
        assertEquals("101", mappedOfMappedProvider.get())

        provider.setValue(2)

        awaitAssertWithDelay {
            assertEquals(2, provider.get())
            assertEquals(2, smth.value)
            assertEquals(102, mappedProvider.get())
            assertEquals("102", mappedOfMappedProvider.get())
        }

        val delegatedValueOnNothing by provider
        assertEquals(2, delegatedValueOnNothing)
    }

    class Smth(valueProvider: Provider<Int>) {
        val value by valueProvider
    }
}