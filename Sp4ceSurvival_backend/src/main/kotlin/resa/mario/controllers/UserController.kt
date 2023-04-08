package resa.mario.controllers

import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import resa.mario.config.APIConfig
import resa.mario.config.security.jwt.JwtTokensUtils
import resa.mario.dto.*
import resa.mario.mappers.toDTOResponse
import resa.mario.models.User
import resa.mario.services.UserService
import java.util.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping(APIConfig.API_PATH)
class UserController
@Autowired constructor(
    private val service: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtils: JwtTokensUtils
) {

    // -- REGISTER, CREATE & LOGIN FOR USERS
    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody userDto: UserDTORegister): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO REGISTER" }

        try {
            val userSaved = service.register(userDto)
            return ResponseEntity.ok(jwtTokenUtils.create(userSaved))
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    suspend fun create(
        @Valid @RequestBody userDto: UserDTOCreate,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO CREATE" }

        try {
            val userSaved = service.create(userDto)
            return ResponseEntity.ok(jwtTokenUtils.create(userSaved))
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    @GetMapping("/login")
    suspend fun login(@Valid @RequestBody userDto: UserDTOLogin): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO LOGIN" }

        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userDto.username,
                userDto.password
            )
        )
        // Now we authenticate the user
        SecurityContextHolder.getContext().authentication = authentication

        // And return the authenticated user
        val user = authentication.principal as User

        return ResponseEntity.ok(jwtTokenUtils.create(user))
    }

    // -- SEARCH METHOD
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/username")
    suspend fun findUserByUsername(
        @AuthenticationPrincipal user: User,
        @RequestParam(defaultValue = "") username: String
    ): ResponseEntity<UserDTOResponse> {
        log.info { "SEARCHING USER WITH USERNAME: $username" }

        val userSearched = service.findByUsername(username)

        return ResponseEntity.ok(userSearched.toDTOResponse())
    }

    // -- LEADERBOARD METHOD
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/leaderboard")
    suspend fun getPagingForLeaderBoard(
        @AuthenticationPrincipal user: User,
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int = 0,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int = 10,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sortBy: String = "scoreNumber",
    ): ResponseEntity<List<UserDtoLeaderBoard>> {
        log.info { "OBTAINING USERS FOR LEADERBOARD, PAGE: $page" }

        val pageResponse = service.findAllForLeaderBoard(page, size, sortBy)

        return ResponseEntity.ok(pageResponse.content)
    }

    // -- ME METHODS --
    @GetMapping("/me")
    suspend fun findMe(@AuthenticationPrincipal user: User): ResponseEntity<UserDTOProfile> {
        log.info { "OBTAINING SELF DATA" }

        return ResponseEntity.ok(service.findScoreByUserId(user))
    }

    @PutMapping("/me/score")
    suspend fun updateScore(@AuthenticationPrincipal user: User, scoreNumber: String): ResponseEntity<String> {
        log.info { "USER: ${user.username} IS UDPATING AN SCORE" }

        val response = service.saveScore(user.id.toString(), scoreNumber)

        return if (!response) {
            ResponseEntity.badRequest().body("SCORE NOT UPDATED")
        } else ResponseEntity.ok("SCORE UPDATED")
    }

    @PutMapping("/me/password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: User,
        @Valid @RequestBody userDTOPasswordUpdate: UserDTOPasswordUpdate
    ): ResponseEntity<User> {
        log.info { "USER: ${user.username} IS TRYING TO UPDATE THE PASSWORD" }

        val userSaved = service.updatePassword(user, userDTOPasswordUpdate)

        return ResponseEntity.ok(userSaved)
    }

    @DeleteMapping("/me/delete")
    suspend fun deleteMe(@AuthenticationPrincipal user: User) {
        log.info { "USER: ${user.username} SELF DELETING ACCOUNT" }

        service.delete(user.username)
    }


    // -- INITIAL DATA METHODS --
    suspend fun createUserInitializer(userDTOCreate: UserDTOCreate): User {
        log.info { "GENERATING INITIAL USER DATA" }
        return service.create(userDTOCreate)
    }

    suspend fun createScoreInitializer(userId: UUID, scoreNumber: String) {
        log.info { "GENERATING INITIAL SCORE DATA" }
        service.saveScore(userId.toString(), scoreNumber)
    }

}