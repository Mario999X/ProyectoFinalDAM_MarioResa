package resa.mario.config.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import resa.mario.config.security.jwt.JwtAuthenticationFilter
import resa.mario.config.security.jwt.JwtAuthorizationFilter
import resa.mario.config.security.jwt.JwtTokensUtils
import resa.mario.services.UserService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig
@Autowired constructor(
    private val service: UserService,
    private val jwtTokensUtils: JwtTokensUtils
) {

    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.userDetailsService(service)
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)

        http
            .csrf()
            .disable()
            .exceptionHandling()
            .and()

            // For the JWT token.
            .authenticationManager(authenticationManager)

            // We don't use session states, therefore a Stateless policy.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            // We accept Http requests
            .authorizeHttpRequests()

            // This allows us to show the errors, instead of getting a code FORBIDDEN.
            .requestMatchers("/error/**").permitAll()

            .requestMatchers("/sp4ceSurvival/**").permitAll()

            //.requestMatchers("/sp4ceSurvival/register", "/sp4ceSurvival/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/sp4ceSurvival/create").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/sp4ceSurvival/username", "/sp4ceSurvival/leaderboard", "/sp4ceSurvival/me").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT,  "/sp4ceSurvival/me/score", "/sp4ceSurvival/me/password").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/sp4ceSurvival/me/delete").hasAnyRole("USER", "ADMIN")

            .anyRequest().authenticated()

            .and()
            // We add our authentication filter.
            .addFilter(JwtAuthenticationFilter(jwtTokensUtils, authenticationManager))
            // And lastly, our authorization filter.
            .addFilter(JwtAuthorizationFilter(jwtTokensUtils, service, authenticationManager))

        return http.build()
    }
}