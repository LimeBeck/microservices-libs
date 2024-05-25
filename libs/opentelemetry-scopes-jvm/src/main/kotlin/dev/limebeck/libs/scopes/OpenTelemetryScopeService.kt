package dev.limebeck.libs.scopes

import dev.limebeck.libs.logger.logger
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context

class OpenTelemetryScopeService(
    private val tracer: Tracer
) : ScopeService<OpenTelemetryScopeService.OpenTelemetryScope> {
    companion object {
        private val logger = OpenTelemetryScopeService::class.logger()
    }

    class OpenTelemetryScope(
        val span: Span,
        description: String,
        params: ScopeParams?
    ) : Scope(
        id = span.spanContext.traceId,
        description = description,
        params = params
    ) {
        override fun finish() {
            span.end()
            super.finish()
        }

        override fun toString(): String {
            return "${super.toString()} <traceId:$id> <spanId:${span.spanContext.spanId}>"
        }
    }

    override suspend fun startScope(description: String, params: ScopeParams?): OpenTelemetryScope {
        val span = tracer.spanBuilder(description).startSpan()
        val scope = OpenTelemetryScope(span, description, params)
        logger.debug { "<d14e02a6> Start scope $scope: $description" }
        return scope
    }

    override suspend fun continueScope(
        scope: OpenTelemetryScope,
        description: String,
        params: ScopeParams?
    ): OpenTelemetryScope {
        val newSpan = tracer
            .spanBuilder(description)
            .setParent(
                Context.current().with(scope.span)
            ).startSpan()
        val newScope = OpenTelemetryScope(newSpan, description, params)
        logger.debug { "$scope <f85a6564> Continue scope with $newScope: $description" }
        return newScope
    }

    override suspend fun logError(scope: OpenTelemetryScope, e: Exception) {
        logger.error(e) { "<68506905> Scope $scope (${scope.description}) got error $e " }
        scope.span.recordException(e)
        scope.span.setStatus(StatusCode.ERROR)
    }

    override suspend fun scopeFinished(scope: OpenTelemetryScope) {
        logger.debug { "<cec98f44> Scope $scope (${scope.description}) finished" }
        scope.finish()
    }
}