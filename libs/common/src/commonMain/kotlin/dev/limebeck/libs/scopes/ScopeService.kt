package dev.limebeck.libs.scopes

typealias ScopeParams = List<Pair<String?, String?>>

interface ScopeService<S : Scope> {
    suspend fun startScope(description: String, params: ScopeParams? = null): S
    suspend fun continueScope(scope: S, description: String, params: ScopeParams? = null): S
    suspend fun logError(scope: S, e: Exception)
    suspend fun scopeFinished(scope: S)
}

suspend inline fun <T, S : Scope> ScopeService<S>.runInScope(
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

suspend inline fun <T, S : Scope> ScopeService<S>.runInScope(
    scope: S,
    description: String,
    operationBody: (S) -> T
): T {
    val newScope = continueScope(scope, description)
    try {
        return operationBody(newScope)
    } catch (e: Exception) {
        logError(newScope, e)
        throw e
    }
}