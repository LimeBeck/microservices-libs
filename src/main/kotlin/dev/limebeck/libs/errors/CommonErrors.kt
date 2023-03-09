package dev.limebeck.libs.errors

class InternalErrorException(
    throwable: Throwable? = null,
    message: String
) : InternalException(CommonErrorCode.INTERNAL_ERROR.toString(), message, throwable) {
    constructor(message: String) : this(null, message)
}

class BadCredentialsException() :
    InternalException(CommonErrorCode.BAD_CREDENTIALS.toString(), "<8fa728fe> Bad credentials", null)

class ObjectNotFoundException(message: String) :
    InternalException(CommonErrorCode.OBJECT_NOT_FOUND.toString(), message, null) {
    constructor() : this("<b7563af2> Object not found")
}

class ObjectAlreadyExistsException(message: String, cause: Throwable?) :
    InternalException(CommonErrorCode.OBJECT_ALREADY_EXISTS.toString(), message, cause) {
    constructor() : this("<4f43d01> Object already exists")
    constructor(message: String) : this(message, null)
}

class MissingParameterException(message: String) :
    InternalException(CommonErrorCode.MISSING_PARAMETER.toString(), message, null)

class AuthorizationRequiredException(message: String) :
    InternalException(CommonErrorCode.AUTHORIZATION_REQUIRED.toString(), message, null)

class AccessDeniedException(message: String) :
    InternalException(CommonErrorCode.ACCESS_DENIED.toString(), message, null)

open class InvalidParameterException(cause: Throwable?, message: String) :
    InternalException(CommonErrorCode.INVALID_PARAMETER.toString(), message, cause) {
    constructor(message: String) : this(null, message)
}

class InvalidOperationException(message: String) :
    InternalException(CommonErrorCode.INVALID_OPERATION.toString(), message, null)

class InvalidTokenException(message: String, cause: Throwable? = null) :
    InternalException(CommonErrorCode.INVALID_TOKEN.toString(), message, cause)
