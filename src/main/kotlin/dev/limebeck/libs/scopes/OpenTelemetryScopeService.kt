package dev.limebeck.libs.scopes

import dev.limebeck.libs.logger.debug
import dev.limebeck.libs.logger.error
import dev.limebeck.libs.logger.getLogger
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context

class OpenTelemetryScopeService(
    private val tracer: Tracer
) : ScopeService {
    companion object {
        private val logger = getLogger()
    }

    class OpenTelemetryScope(
        val span: Span,
        description: String
    ) : Scope(
        id = span.spanContext.traceId,
        description = description,
    ) {
        override fun finish() {
            span.end()
            super.finish()
        }

        override fun toString(): String {
            return "${super.toString()} <traceId:$id> <spanId:${span.spanContext.spanId}>"
        }
    }

    override fun startScope(description: String): Scope {
        val span = tracer.spanBuilder(description).startSpan()
        val scope = OpenTelemetryScope(span, description)
        logger.debug { "<d14e02a6> Start scope $scope: $description" }
        return scope
    }

    override fun continueScope(scope: Scope, description: String): Scope {
        if (scope is OpenTelemetryScope) {
            val newSpan = tracer
                .spanBuilder(description)
                .setParent(
                    Context.current().with(scope.span)
                ).startSpan()
            val newScope = OpenTelemetryScope(newSpan, description)
            logger.debug { "$scope <f85a6564> Continue scope with $newScope: $description" }
            return newScope
        } else {
            throw RuntimeException("<a108fec6> Expected OpenTelemetryScope, got ${scope::class.qualifiedName}")
        }
    }

    override fun logError(scope: Scope, e: Exception) {
        logger.error(e) { "<68506905> Scope $scope (${scope.description}) got error $e " }
        if (scope is OpenTelemetryScope) {
            scope.span.recordException(e)
            scope.span.setStatus(StatusCode.ERROR)
        }
    }

    override fun scopeFinished(scope: Scope) {
        logger.debug { "<cec98f44> Scope $scope (${scope.description}) finished" }
        scope.finish()
    }
}