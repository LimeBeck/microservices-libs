package dev.limebeck.libs.errors

open class InternalException(
    val code: String,
    val uid: String? = null,
    val messageWithoutUid: String = "",
    cause: Throwable? = null
) : RuntimeException(listOfNotNull(uid, messageWithoutUid).joinToString(" "), cause) {

    private constructor(
        result: String,
        parsedMessage: ParsedMessage?,
        cause: Throwable? = null
    ) : this(result, parsedMessage?.uid, parsedMessage?.msg ?: "", cause)

    constructor(
            result: String,
            message: String? = null,
            cause: Throwable? = null
    ) : this(result, message?.let { parseMessage(it) }, cause)

    companion object {

        private val UID_PATTERN = "<([0-9a-f]+)>".toRegex()

        private class ParsedMessage(val uid: String?, val msg: String)

        private fun parseMessage(msg: String): ParsedMessage {
            return UID_PATTERN.find(msg)?.let { m ->
                ParsedMessage(m.groupValues[1],
                        (msg.substring(0, m.range.first) + msg.substring(m.range.last + 1)).trim())
            } ?: ParsedMessage(null, msg)
        }
    }
}
