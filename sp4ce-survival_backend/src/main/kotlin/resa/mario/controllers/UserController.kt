package resa.mario.controllers

import com.github.michaelbull.result.*
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
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
import resa.mario.exceptions.TokenError
import resa.mario.exceptions.UserDataBaseConflict
import resa.mario.exceptions.UserException.*
import resa.mario.mappers.toDTOResponse
import resa.mario.mappers.toScoreDTO
import resa.mario.models.User
import resa.mario.services.UserService
import resa.mario.validators.validate
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * RestController that will manage every endpoint related to users and scores using the different
 * functions from the service.
 *
 * @property service
 * @property authenticationManager
 * @property jwtTokenUtils
 */
@RestController
@RequestMapping(APIConfig.API_PATH)
class UserController
@Autowired constructor(
    private val service: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtils: JwtTokensUtils
) {

    // -- REGISTER, CREATE & LOGIN FOR USERS

    /**
     * Function to register new users by themselves.
     *
     * @param userDto [UserDTORegister]
     * @return A personal Token for that user.
     */
    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody userDto: UserDTORegister): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO REGISTER" }

        try {
            return when (val userResult = userDto.validate()) {
                is Ok -> {
                    val userSaved = service.register(userResult.value)
                    ResponseEntity.status(HttpStatus.CREATED).body(jwtTokenUtils.create(userSaved))
                }

                is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResult.error.message)
            }
        } catch (e: DataIntegrityViolationException) {
            throw UserDataBaseConflict("USERNAME OR EMAIL ALREADY IN USE")
        }

    }

    /**
     * Function to create new users by an Admin.
     *
     * @param userDto [UserDTOCreate]
     * @param user Token from an Admin.
     * @return A unique Token for that user to the Admin, a bad request if the [UserDTOCreate] is invalid or exceptions.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    suspend fun create(
        @Valid @RequestBody userDto: UserDTOCreate,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO CREATE" }

        try {
            return when (val userResult = userDto.validate()) {
                is Ok -> {
                    val userSaved = service.create(userResult.value)
                    ResponseEntity.status(HttpStatus.CREATED).body(jwtTokenUtils.create(userSaved))
                }

                is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResult.error.message)
            }

        } catch (e: DataIntegrityViolationException) {
            throw UserDataBaseConflict("USERNAME OR EMAIL ALREADY IN USE")
        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }
    }

    /**
     * Function to log in.
     *
     * @param userDto [UserDTOLogin]
     * @return A personal token for that user or a bad request if the [UserDTOLogin] is invalid
     */
    @GetMapping("/login")
    suspend fun login(@Valid @RequestBody userDto: UserDTOLogin): ResponseEntity<String> {
        log.info { "USER: ${userDto.username} TRYING TO LOGIN" }

        return when (val userResult = userDto.validate()) {
            is Ok -> {
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

                ResponseEntity.ok(jwtTokenUtils.create(user))
            }

            is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResult.error.message)
        }

    }

    // -- SEARCH METHOD

    /**
     * Function to search an existing user
     *
     * @param user Token from an existing user
     * @param username Username of the user to search
     * @return A possible user found, [UserDTOResponse] or a not found message
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/username")
    suspend fun findUserByUsername(
        @AuthenticationPrincipal user: User,
        @RequestParam(defaultValue = "") username: String
    ): ResponseEntity<out Any> {
        log.info { "SEARCHING USER WITH USERNAME: $username" }

        return when (val serviceResult = service.findByUsername(username)) {
            is Ok -> ResponseEntity.status(HttpStatus.OK).body(serviceResult.value.toDTOResponse())
            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(serviceResult.error.message)
        }
    }


    // -- LEADERBOARD METHOD

    /**
     * Function to obtain a list of users with scores associated.
     *
     * @param user Token from an existing user
     * @param page Page to retrieve data
     * @param size Size of the page/list
     * @param sortBy Sort by X, in this case, [ScoreDTOResponse.scoreNumber]
     * @return A possible list of users with scores, [UserDTOLeaderBoard] or a Not Found message
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/leaderboard")
    suspend fun getPagingForLeaderBoard(
        @AuthenticationPrincipal user: User,
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int = 0,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int = 10,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sortBy: String = "scoreNumber",
    ): ResponseEntity<out Any> {
        log.info { "OBTAINING USERS FOR LEADERBOARD, PAGE: $page" }

        return when (val pageResponse = service.findAllForLeaderBoard(page, size, sortBy)) {
            is Ok -> ResponseEntity.ok(pageResponse.value.content)
            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(pageResponse.error.message)
        }
    }

    // -- ME METHODS --

    /**
     * Function to retrieve self information.
     *
     * @param user Token from an existing user
     * @return [UserDTOProfile], except if the token is not correct or expired.
     */
    @GetMapping("/me")
    suspend fun findMe(@AuthenticationPrincipal user: User): ResponseEntity<out Any> {
        log.info { "OBTAINING SELF DATA" }

        try {
            return when (val serviceResult = service.findUserProfile(user)) {
                is Ok -> ResponseEntity.ok(serviceResult.value)
                is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult.error.message)
            }
        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }
    }

    /**
     * Function to save/update a self score.
     *
     * @param user Token from an existing user
     * @param scoreNumber Score number obtained
     * @return A message confirming o denying of the saved/updated score.
     */
    @PutMapping("/me/score")
    suspend fun updateScore(@AuthenticationPrincipal user: User, scoreNumber: String): ResponseEntity<String> {
        log.info { "USER: ${user.username} IS UDPATING AN SCORE" }

        try {
            val response = service.saveScore(user.id.toString(), scoreNumber)

            return if (!response) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SCORE NOT HIGHER THAN ACTUAL REGISTERED")
            } else ResponseEntity.ok("SCORE UPDATED")

        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }
    }

    /**
     * Function to update a self password.
     *
     * @param user Token from an existing user
     * @param userDTOPasswordUpdate [UserDTOPasswordUpdate]
     * @return A message confirming o denying of the updated password.
     */
    @PutMapping("/me/password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: User,
        @Valid @RequestBody userDTOPasswordUpdate: UserDTOPasswordUpdate
    ): ResponseEntity<String> {
        log.info { "USER: ${user.username} IS TRYING TO UPDATE THE PASSWORD" }

        try {
            return when (val userResult = userDTOPasswordUpdate.validate()) {
                is Ok -> {
                    when (val serviceResult = service.updatePassword(user, userDTOPasswordUpdate)) {
                        is Ok -> ResponseEntity.ok("USER UPDATED")

                        is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult.error.message)
                    }
                }

                is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResult.error.message)
            }

        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }

    }

    /**
     * Function to delete a user by themselves.
     *
     * @param user Token from an existing user
     * @return A response entity of type No Content or a message denying because the token is expired or is manipulated and the user don't exist.
     */
    @DeleteMapping("/me")
    suspend fun deleteMe(@AuthenticationPrincipal user: User): ResponseEntity<String> {
        log.info { "USER: ${user.username} SELF DELETING ACCOUNT" }

        try {
            return when (val deletedUser = service.delete(user.username)) {
                is Ok -> {
                    log.info { "USER: ${deletedUser.value.username} HAS BEEN DELETED" }
                    ResponseEntity.noContent().build()
                }

                is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(deletedUser.error.message)
            }
        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }
    }

    // -- SCORES METHODS --

    /**
     * Function to obtain a possible [ScoreDTOResponse] from an existing user.
     *
     * @param user Token from an existing user
     * @return A possible [ScoreDTOResponse]
     */
    @GetMapping("/score")
    suspend fun getScore(@AuthenticationPrincipal user: User): ResponseEntity<ScoreDTOResponse> {
        log.info { "USER: ${user.username} OBTAINING SELF SCORE" }

        try {
            val score = service.findScoreByUserId(user.id!!)

            return if (score != null) {
                ResponseEntity.ok(score.toScoreDTO())
            } else {
                ResponseEntity.noContent().build()
            }

        } catch (e: NullPointerException) {
            throw TokenError("TOKEN ERROR")
        }
    }

    // -- INITIAL DATA METHODS --

    /**
     * Function without endpoint to load initial data
     *
     * @param userDTOCreate [UserDTOCreate]
     * @return A possible [User]
     */
    suspend fun createUserInitializer(userDTOCreate: UserDTOCreate): User? {
        log.info { "GENERATING INITIAL USER DATA" }
        return service.create(userDTOCreate)
    }

    /**
     * Function without endpoint to load initial data
     *
     * @param userId [UUID] from an existing user
     * @param scoreNumber Number of the score, String
     */
    suspend fun createScoreInitializer(userId: UUID, scoreNumber: String) {
        log.info { "GENERATING INITIAL SCORE DATA" }
        service.saveScore(userId.toString(), scoreNumber)
    }

}