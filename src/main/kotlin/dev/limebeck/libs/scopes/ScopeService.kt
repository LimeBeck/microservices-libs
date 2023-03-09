package dev.limebeck.libs.scopes

interface ScopeService {
    fun startScope(description: String): Scope
    fun continueScope(scope: Scope, description: String): Scope
    fun logError(scope: Scope, e: Exception)
    fun scopeFinished(scope: Scope)
}

inline fun <T> ScopeService.runInScope(
    description: String,
    operationBody: (Scope) -> T
): T {
    val scope = startScope(description)
    try {
        return operationBody(scope)
    } catch (e: Exception) {
        logError(scope, e)
        throw e
    } finally {
        scopeFinished(scope)
    }
}

inline fun <T> ScopeService.runInScope(scope: Scope, description: String, operationBody: (Scope) -> T): T {
    val newScope = continueScope(scope, description)
    try {
        return operationBody(newScope)
    } catch (e: Exception) {
        logError(newScope, e)
        throw e
    }
}