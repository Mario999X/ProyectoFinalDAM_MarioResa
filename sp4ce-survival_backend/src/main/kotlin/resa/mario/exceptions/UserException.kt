package resa.mario.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Main class extending [RuntimeException] for personal exceptions
 *
 * @constructor
 * A possible message, String
 *
 * @param message
 */
sealed class UserException(message: String?) : RuntimeException(message) {
    /**
     * For Bad Request Response
     *
     * @constructor
     * A possible message, String
     *
     * @param message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class UserExceptionBadRequest(message: String?) : UserException(message)

    /**
     * For a Not Found Response
     *
     * @constructor
     * A possible message, String
     *
     * @param message
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    class UserExceptionNotFound(message: String?) : UserException(message)
}

/**
 * For a Database conflict Error Response
 *
 * @constructor
 * A possible message, String
 *
 * @param message
 */
@ResponseStatus(HttpStatus.CONFLICT)
class UserDataBaseConflict(message: String?) : Exception(message)

/**
 * For a Token error Response
 *
 * @constructor
 * A possible message, String
 *
 * @param message
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenExpired(message: String?) : Exception(message)


