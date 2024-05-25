package dev.limebeck.libs.scopes

import dev.limebeck.testUtils.runTest
import kotlin.test.Test

class DateBasedIdGeneratorTest {

    @Test
    fun generateId() = runTest {
        val generator = DateBasedIdGenerator()
        val id = generator.generateId()
        println(id)
        val id2 = generator.generateId()
        println(id2)
    }
}