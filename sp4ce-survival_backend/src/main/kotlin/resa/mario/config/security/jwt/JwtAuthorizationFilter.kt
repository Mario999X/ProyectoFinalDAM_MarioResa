package resa.mario.config.security.jwt

import io.netty.handler.codec.http.HttpHeaderNames
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import resa.mario.services.UserService
import java.io.IOException

private val log = KotlinLogging.logger {}

/**
 * Class for creating an authorization filter for Spring Security.
 *
 * @property jwtTokensUtils
 * @property service
 * @constructor
 * It uses the class for generating tokens [JwtTokensUtils], the User Service [UserService]
 * and an Authentication Manager.
 *
 * @param authManager
 */
class JwtAuthorizationFilter(
    private val jwtTokensUtils: JwtTokensUtils,
    private val service: UserService,
    authManager: AuthenticationManager
) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        log.info { "Filtering..." }
        val header = req.getHeader(HttpHeaderNames.AUTHORIZATION.toString())

        if (header == null || !header.startsWith(JwtTokensUtils.TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }
        getAuthentication(header.substring(7))?.also {
            SecurityContextHolder.getContext().authentication = it
        }
        chain.doFilter(req, res)
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? = runBlocking {
        log.info { "Getting authentication..." }

        val tokenDecoded = jwtTokensUtils.decode(token) ?: return@runBlocking null

        val username = tokenDecoded.getClaim("username").toString().replace("\"", "")

        val user = service.loadUserByUsername(username) ?: return@runBlocking null

        return@runBlocking UsernamePasswordAuthenticationToken(
            user,
            null,
            user.authorities
        )
    }
}