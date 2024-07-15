package dev.limebeck.libs.errors

object CommonErrorCodes {
    /**
     * Operation success
     */
    val SUCCESS = "SUCCESS"

    /**
     * Uncategorized internal server error
     */
    val INTERNAL_ERROR = "INTERNAL_ERROR"

    /**
     * Invalid input parameter
     */
    val INVALID_PARAMETER = "INVALID_PARAMETER"

    /**
     * Missing input parameter
     */
    val MISSING_PARAMETER = "MISSING_PARAMETER"

    /**
     * Session provided but invalid or expired
     */
    val INVALID_SESSION = "INVALID_SESSION"

    /**
     * Access to method denied caused by insufficient rights
     */
    val ACCESS_DENIED = "ACCESS_DENIED"

    /**
     * Access to protected method without authorization
     */
    val AUTHORIZATION_REQUIRED = "AUTHORIZATION_REQUIRED"

    /**
     * Queried object not found
     */
    val OBJECT_NOT_FOUND = "OBJECT_NOT_FOUND"

    /**
     * Queried method not found
     */
    val METHOD_NOT_FOUND = "METHOD_NOT_FOUND"

    /**
     * Same object already exists
     */
    val OBJECT_ALREADY_EXISTS = "OBJECT_ALREADY_EXISTS"

    /**
     * Invalid authentication information, wrong login or password
     */
    val BAD_CREDENTIALS = "BAD_CREDENTIALS"

    /**
     * Invalid user operation. Operation not applicable to specified object
     */
    val INVALID_OPERATION = "INVALID_OPERATION"

    /**
     * Permitted time of operation was exceeded
     */
    val OPERATION_TIMEOUT = "OPERATION_TIMEOUT"

    /**
     * Invalid access or refresh token
     */
    val INVALID_TOKEN = "INVALID_TOKEN"
}