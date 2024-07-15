package dev.limebeck.libs.scopes

import dev.limebeck.libs.logger.logger


abstract class Scope(
    open val id: String,
    open val description: String,
    open val params: ScopeParams?,
) {

    companion object {
        val EMPTY = object : Scope("EMPTY", "", null) {}
        val logger = Scope::class.logger()
    }

    override fun toString(): String {
        return "<scope:$id>"
    }

    open fun finish() {
        logger.debug { "$this <b999e7d5> Scope finished" }
    }
}