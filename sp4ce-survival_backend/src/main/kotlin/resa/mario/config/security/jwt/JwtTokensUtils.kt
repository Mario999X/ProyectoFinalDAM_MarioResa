package resa.mario.config.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import resa.mario.models.User
import java.util.*

/**
 * Component class for creating, verifying and decoding tokens using JWT.
 *
 */
@Component
class JwtTokensUtils {

    @Value("\${jwt.secret}")
    private val secretJwt: String? = null

    /**
     * Token Creation Method
     *
     * @param user
     * @return Token created, String
     */
    fun create(user: User): String {
        return JWT.create()
            .withSubject(user.id.toString())
            .withHeader(mapOf("typ" to TOKEN_TYPE))
            .withClaim("username", user.username)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + (24 * 60 * 60 * 1_000) * 2)) // Two days
            .sign(Algorithm.HMAC512(secretJwt))
    }

    /**
     * Token Decoding Method
     *
     * @param token
     * @return A possible Token decoded, [DecodedJWT]
     */
    fun decode(token: String): DecodedJWT? {
        val verifier = JWT.require(Algorithm.HMAC512(secretJwt))
            .build()

        return try {
            verifier.verify(token)
        } catch (_: Exception) {
            null
        }

    }

    companion object {
        const val TOKEN_HEADER = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
        const val TOKEN_TYPE = "JWT"
    }
}