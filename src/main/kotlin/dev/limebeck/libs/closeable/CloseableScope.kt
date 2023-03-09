package dev.limebeck.libs.closeable

import dev.limebeck.libs.logger.logger


typealias Callback = () -> Unit

interface CloseableScopeRegistrar {
    fun onClose(name: String? = null, callback: Callback)
}

inline fun <reified T : AutoCloseable> T.onClose(scope: CloseableScopeRegistrar, name: String? = null) = this.apply {
    scope.onClose(name, ::close)
}

class CloseableScope : CloseableScopeRegistrar {
    companion object {
        private val logger = CloseableScope::class.logger
    }

    private val callbacks = mutableListOf<Pair<Callback, String?>>()

    override fun onClose(name: String?, callback: Callback) {
        callbacks.add(callback to name)
    }

    fun closeAll() {
        callbacks.forEach {
            try {
                it.first()
                logger.debug("Callback ${it.second ?: ""} executed")
            } catch (e: Throwable) {
                logger.error("Can`t execute callback ${it.second ?: "for unknown resource"} because of", e)
            }
        }
    }
}

/**
 * Позволяет выполнить зарегистрированные колбеки после выполнения функции
 * даже в случае ошибки выполнения
 *
 * Пример использования:
 * ```kotlin
 *  scoped { scope ->
 *      val file = ZipFile(this).register(scope, "for zip file") //Закроет Autocloseable по достижению конца блока `scoped`
 *      scope.register {
 *          println("Some log")
 *      }
 *  }
 * ```
 */
inline fun <reified T> scoped(block: (scope: CloseableScopeRegistrar) -> T): T {
    val scope = CloseableScope()
    return try {
        block(scope)
    } finally {
        scope.closeAll()
    }
}