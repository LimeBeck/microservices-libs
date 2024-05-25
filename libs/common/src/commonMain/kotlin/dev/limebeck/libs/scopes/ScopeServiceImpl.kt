package dev.limebeck.libs.scopes

import dev.limebeck.libs.logger.logger


class ScopeServiceImpl(
    val idGenerator: ScopeIdGenerator,
    val isInternalErrorDetector: (e: Throwable) -> Boolean
) : ScopeService<ScopeServiceImpl.ScopeImpl> {

    companion object {
        private val logger = ScopeServiceImpl::class.logger()
    }

    class ScopeImpl(
        id: String,
        description: String,
        params: ScopeParams?
    ) : Scope(id, description, params) {
        private var associatedScopes = mutableListOf<ScopeImpl>()

        fun associateWith(scope: ScopeImpl) {
            logger.debug { "<3c40f88c> $this associated with $scope" }
            associatedScopes.add(scope)
        }

        private fun fillIdList(): List<String> {
            return associatedScopes.flatMap { scope -> scope.fillIdList() } + listOf(id)
        }

        override fun toString(): String {
            val list = fillIdList()
            return "<scope:${list.joinToString(",")}>"
        }

        fun toLogString(): String =
            ((listOf(null to description) + (params ?: listOf())))
                .asSequence()
                .map { (key, value) ->
                    listOfNotNull(key, value).joinToString(": ")
                }.joinToString(", ")

        override fun finish() {
            logger.debug { "<992d0791> $this Scope finished" }
        }

        fun clone(): ScopeImpl = ScopeImpl(
            id = id,
            description = description,
            params = params
        ).also { copiedScope ->
            copiedScope.associatedScopes.addAll(associatedScopes.map { it.clone() })
        }
    }

    override suspend fun startScope(description: String, params: ScopeParams?): ScopeImpl {
        val scope = ScopeImpl(idGenerator.generateId(), description, params)
        logger.debug { "<52066cdc> $scope Scope started: ${scope.toLogString()}" }
        return scope
    }

    override suspend fun continueScope(
        scope: ScopeImpl,
        description: String,
        params: ScopeParams?
    ): ScopeImpl {
        val newScope = ScopeImpl(idGenerator.generateId(), description, params)
        newScope.associateWith(scope)
        val values = ((listOf(null to description) + (params ?: listOf())))
            .asSequence()
            .map { (key, value) ->
                listOfNotNull(key, value).joinToString(": ")
            }.joinToString(", ")
        logger.debug { "<52066cdc> $newScope Scope started: $values" }
        return newScope
    }

    override suspend fun logError(scope: ScopeImpl, e: Exception) {
        if (isInternalErrorDetector(e))
            logger.error(e) { "<92b33828> $scope Operation failed" }
        else
            logger.debug { "<b18437d3> $scope Operation failed: ${e.message}" }
    }

    override suspend fun scopeFinished(scope: ScopeImpl) {
        scope.finish()
    }
}