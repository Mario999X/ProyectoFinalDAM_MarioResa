package resa.mario.controllers

import com.github.michaelbull.result.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
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
import resa.mario.exceptions.UserException.*
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
    @Operation(summary = "Register", description = "Endpoint for registering", tags = ["USER"])
    @Parameter(name = "userDTO", description = "Valid user DTO for resistering", required = true)
    @ApiResponse(responseCode = "201", description = "A personal Token for that user.")
    @ApiResponse(responseCode = "209", description = "If the username or the email is already in use.")
    @ApiResponse(responseCode = "400", description = "If the userDTO is not validated")
    @PostMapping("/register")
    suspend fun register(@RequestBody userDto: UserDTORegister): ResponseEntity<GenericResponse> {
        log.info { "USER: ${userDto.username} TRYING TO REGISTER" }

        return when (val userResult = userDto.validate()) {
            is Ok -> {
                when (val userSaved = service.register(userResult.value)) {
                    is Ok -> ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse(jwtTokenUtils.create(userSaved.value)))

                    is Err -> ResponseEntity.status(HttpStatus.CONFLICT).body(GenericResponse(userSaved.error.message!!))
                }
            }

            is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse(userResult.error.message!!))
        }
    }

    /**
     * Function to create new users by an Admin.
     *
     * @param userDto [UserDTOCreate]
     * @param user Token from an Admin.
     * @return A unique Token for that user to the Admin, a bad request if the [UserDTOCreate] is invalid or exceptions.
     */
    @Operation(summary = "Create", description = "Endpoint for creating new users from an Admin", tags = ["USER"])
    @Parameter(name = "userDTO", description = "Valid user DTO for resistering", required = true)
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @ApiResponse(responseCode = "201", description = "A personal Token for that user.")
    @ApiResponse(responseCode = "209", description = "If the username or the email is already in use.")
    @ApiResponse(responseCode = "400", description = "If the userDTO is not validated.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    suspend fun create(
        @RequestBody userDto: UserDTOCreate,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<GenericResponse> {
        log.info { "USER: ${userDto.username} TRYING TO CREATE" }

        return when (val userResult = userDto.validate()) {
            is Ok -> {
                when (val userSaved = service.create(userResult.value)) {
                    is Ok -> ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse(jwtTokenUtils.create(userSaved.value)))
                    is Err -> ResponseEntity.status(HttpStatus.CONFLICT).body(GenericResponse(userSaved.error.message!!))
                }
            }

            is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse(userResult.error.message!!))
        }
    }

    /**
     * Function to log in.
     *
     * @param userDto [UserDTOLogin]
     * @return A personal token for that user or a bad request if the [UserDTOLogin] is invalid
     */
    @Operation(summary = "Login", description = "Endpoint for log in.", tags = ["USER"])
    @Parameter(name = "userDTO", description = "Valid user DTO for logging in", required = true)
    @ApiResponse(responseCode = "200", description = "A personal Token for that user.")
    @ApiResponse(responseCode = "400", description = "If the userDTO is not validated.")
    @ApiResponse(responseCode = "404", description = "If the user is not found.")
    @GetMapping("/login")
    suspend fun login(@RequestBody userDto: UserDTOLogin): ResponseEntity<GenericResponse> {
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

                ResponseEntity.ok(GenericResponse(jwtTokenUtils.create(user)))
            }

            is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse(userResult.error.message!!))
        }
    }

    // -- SEARCH METHOD

    /**
     * Function to search an existing user
     *
     * @param user Token from an existing user
     * @param username Username of the user to search
     * @return A possible user found, [GenericResponse] or a not found message
     */
    @Operation(
        summary = "Find by username",
        description = "Endpoint for searching an user through the username",
        tags = ["USER"]
    )
    @Parameter(name = "username", description = "Username of the user to search.", required = true)
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @ApiResponse(responseCode = "200", description = "A response of UserDTOResponse")
    @ApiResponse(responseCode = "404", description = "If the user is not found.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/username")
    suspend fun findUserByUsername(
        @AuthenticationPrincipal user: User,
        @RequestParam(defaultValue = "") username: String
    ): ResponseEntity<GenericResponse> {
        log.info { "SEARCHING USER WITH USERNAME: $username" }

        return when (val serviceResult = service.findByUsername(username)) {
            is Ok -> ResponseEntity.status(HttpStatus.OK).body(GenericResponse(serviceResult.value.username))
            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(serviceResult.error.message!!))
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
    @Operation(
        summary = "Get list of users with scores in order",
        description = "Endpoint for obtaining a list of users with scores associated",
        tags = ["USER"]
    )
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @Parameter(name = "page", description = "Page number", required = false)
    @Parameter(name = "size", description = "Size of the page.", required = false)
    @Parameter(name = "sortBy", description = "How to sort the contents of the page", required = false)
    @ApiResponse(responseCode = "200", description = "The content of the page.")
    @ApiResponse(responseCode = "404", description = "If the page is not found.")
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
            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(pageResponse.error.message!!))
        }
    }

    // -- ME METHODS --

    /**
     * Function to retrieve self information.
     *
     * @param user Token from an existing user
     * @return [UserDTOProfile], except if the token is not correct or expired.
     */
    @Operation(summary = "Find own data", description = "Endpoint for obtaining self data.", tags = ["USER"])
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @ApiResponse(responseCode = "200", description = "The profile of that user.")
    @ApiResponse(responseCode = "404", description = "If the user is not found.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    suspend fun findMe(@AuthenticationPrincipal user: User): ResponseEntity<out Any> {
        log.info { "OBTAINING SELF DATA" }

        return when (val serviceResult = service.findUserProfile(user)) {
            is Ok -> ResponseEntity.ok(serviceResult.value)
            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(serviceResult.error.message!!))
        }
    }

    /**
     * Function to save/update a self score.
     *
     * @param user Token from an existing user
     * @param scoreNumber Score number obtained
     * @return A message confirming o denying of the saved/updated score.
     */
    @Operation(
        summary = "Save or Update the own score",
        description = "Endpoint for uploading a self score",
        tags = ["USER"]
    )
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @Parameter(name = "scoreNumber", description = "Number associated with the score.", required = true)
    @ApiResponse(responseCode = "200", description = "Score updated")
    @ApiResponse(responseCode = "400", description = "Score not updated")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/me/score")
    suspend fun updateScore(@AuthenticationPrincipal user: User, scoreNumber: String): ResponseEntity<GenericResponse> {
        log.info { "USER: ${user.username} IS UDPATING AN SCORE" }

        val response = service.saveScore(user.id.toString(), scoreNumber)

        return if (!response) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse("SCORE NOT HIGHER THAN ACTUAL REGISTERED"))
        } else ResponseEntity.ok(GenericResponse("SCORE UPDATED"))
    }

    /**
     * Function to update a self password.
     *
     * @param user Token from an existing user
     * @param userDTOPasswordUpdate [UserDTOPasswordUpdate]
     * @return A message confirming o denying of the updated password.
     */
    @Operation(summary = "Update own password.", description = "Endpoint for updating own password", tags = ["USER"])
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @Parameter(name = "userDTOPasswordUpdate", description = "UserDTOPasswordUpdate", required = true)
    @ApiResponse(responseCode = "200", description = "If the password was updated successfully")
    @ApiResponse(responseCode = "400", description = "If the UserDTOPasswordUpdate was not validated")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/me/password")
    suspend fun updatePassword(
        @AuthenticationPrincipal user: User,
        @RequestBody userDTOPasswordUpdate: UserDTOPasswordUpdate
    ): ResponseEntity<GenericResponse> {
        log.info { "USER: ${user.username} IS TRYING TO UPDATE THE PASSWORD" }

        return when (val userResult = userDTOPasswordUpdate.validate()) {
            is Ok -> {
                when (val serviceResult = service.updatePassword(user, userDTOPasswordUpdate)) {
                    is Ok -> ResponseEntity.ok(GenericResponse("USER UPDATED"))

                    is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse(serviceResult.error.message!!))
                }
            }

            is Err -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse(userResult.error.message!!))
        }
    }

    /**
     * Function to delete a user by themselves.
     *
     * @param user Token from an existing user
     * @return A response entity of type No Content or a message denying because the token is expired or is manipulated and the user don't exist.
     */
    @Operation(summary = "Delete own user", description = "Endpoint for deleting self account.", tags = ["USER"])
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @ApiResponse(responseCode = "204", description = "The user was deleted.")
    @ApiResponse(responseCode = "404", description = "If user was not found.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/me")
    suspend fun deleteMe(@AuthenticationPrincipal user: User): ResponseEntity<GenericResponse> {
        log.info { "USER: ${user.username} SELF DELETING ACCOUNT" }

        return when (val deletedUser = service.delete(user.username)) {
            is Ok -> {
                log.info { "USER: ${deletedUser.value.username} HAS BEEN DELETED" }
                ResponseEntity.noContent().build()
            }

            is Err -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(deletedUser.error.message!!))
        }
    }

    // -- SCORES METHODS --

    /**
     * Function to obtain a possible [ScoreDTOResponse] from an existing user.
     *
     * @param user Token from an existing user
     * @return A possible [ScoreDTOResponse]
     */
    @Operation(
        summary = "Obtaining self score",
        description = "Endpoint for obtaining a possible self score",
        tags = ["USER"]
    )
    @Parameter(name = "user", description = "Token for authentication.", required = true)
    @ApiResponse(responseCode = "200", description = "If an score is found.")
    @ApiResponse(responseCode = "204", description = "If there is no score found.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/score")
    suspend fun getScore(@AuthenticationPrincipal user: User): ResponseEntity<ScoreDTOResponse> {
        log.info { "USER: ${user.username} OBTAINING SELF SCORE" }

        val score = service.findScoreByUserId(user.id!!)

        return if (score != null) {
            ResponseEntity.ok(score.toScoreDTO())
        } else {
            ResponseEntity.noContent().build()
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
        return service.create(userDTOCreate).component1()
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

    suspend fun getAllUsersOnly10Initializer(): List<User>{
        log.info { "OBTAINING INITIAL USERS" }
        return service.findAllOnly10()
    }

}
