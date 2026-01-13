package dev.limebeck.libs.errors

class InternalErrorException(
    throwable: Throwable? = null,
    message: String
) : InternalException(CommonErrorCodes.INTERNAL_ERROR, message, throwable) {
    constructor(message: String) : this(null, message)
}

class BadCredentialsException() :
    InternalException(CommonErrorCodes.BAD_CREDENTIALS, "<8fa728fe> Bad credentials", null)

class ObjectNotFoundException(message: String) :
    InternalException(CommonErrorCodes.OBJECT_NOT_FOUND, message, null) {
    constructor() : this("<b7563af2> Object not found")
}

class ObjectAlreadyExistsException(message: String, cause: Throwable?) :
    InternalException(CommonErrorCodes.OBJECT_ALREADY_EXISTS, message, cause) {
    constructor() : this("<4f43d01> Object already exists")
    constructor(message: String) : this(message, null)
}

class MissingParameterException(message: String) :
    InternalException(CommonErrorCodes.MISSING_PARAMETER, message, null)

class AuthorizationRequiredException(message: String) :
    InternalException(CommonErrorCodes.AUTHORIZATION_REQUIRED, message, null)

class AccessDeniedException(message: String) :
    InternalException(CommonErrorCodes.ACCESS_DENIED, message, null)

open class InvalidParameterException(cause: Throwable?, message: String) :
    InternalException(CommonErrorCodes.INVALID_PARAMETER, message, cause) {
    constructor(message: String) : this(null, message)
}

class InvalidOperationException(message: String) :
    InternalException(CommonErrorCodes.INVALID_OPERATION, message, null)

class InvalidTokenException(message: String, cause: Throwable? = null) :
    InternalException(CommonErrorCodes.INVALID_TOKEN, message, cause)
