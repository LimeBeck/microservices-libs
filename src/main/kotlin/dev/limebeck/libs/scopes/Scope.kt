package dev.limebeck.libs.scopes

import dev.limebeck.libs.logger.getLogger


abstract class Scope(
    open val id: String,
    open val description: String
) {

    companion object {
        val EMPTY = object : Scope("EMPTY", "") {}
        val logger = getLogger()
    }

    override fun toString(): String {
        return "<scope:$id>"
    }

    open fun finish() {
        logger.debug("<87d4f41b> $this Scope finished")
    }
}