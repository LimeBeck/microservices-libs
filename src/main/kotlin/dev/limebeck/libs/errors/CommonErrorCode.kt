package dev.limebeck.libs.errors

enum class CommonErrorCode {
    /**
     * Operation success
     */
    SUCCESS,

    /**
     * Uncategorized internal server error
     */
    INTERNAL_ERROR,

    /**
     * Invalid input parameter
     */
    INVALID_PARAMETER,

    /**
     * Missing input parameter
     */
    MISSING_PARAMETER,

    /**
     * Session provided but invalid or expired
     */
    INVALID_SESSION,

    /**
     * Access to method denied caused by insufficient rights
     */
    ACCESS_DENIED,

    /**
     * Access to protected method without authorization
     */
    AUTHORIZATION_REQUIRED,

    /**
     * Queried object not found
     */
    OBJECT_NOT_FOUND,

    /**
     * Queried method not found
     */
    METHOD_NOT_FOUND,

    /**
     * Same object already exists
     */
    OBJECT_ALREADY_EXISTS,

    /**
     * Invalid authentication information, wrong login or password
     */
    BAD_CREDENTIALS,

    /**
     * Invalid user operation. Operation not applicable to specified object
     */
    INVALID_OPERATION,

    /**
     * Permitted time of operation was exceeded
     */
    OPERATION_TIMEOUT,

    /**
     * Invalid access or refresh token
     */
    INVALID_TOKEN

}