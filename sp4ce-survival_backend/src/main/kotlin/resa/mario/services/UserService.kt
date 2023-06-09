package resa.mario.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import resa.mario.dto.*
import resa.mario.exceptions.UserException
import resa.mario.exceptions.UserException.*
import resa.mario.mappers.toDTOProfile
import resa.mario.mappers.toScore
import resa.mario.mappers.toScoreDTO
import resa.mario.mappers.toUser
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepositoryCached
import resa.mario.repositories.user.UserRepositoryCached
import java.time.LocalDate
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Service that will execute functions from the different repositories.
 *
 * @property userRepositoryCached
 * @property scoreRepositoryCached
 * @property passwordEncoder
 */
@Service
class UserService
@Autowired constructor(
    private val userRepositoryCached: UserRepositoryCached,
    private val scoreRepositoryCached: ScoreRepositoryCached,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    // -- USERS --

    /**
     * Special Function from Spring Security that searches a user using the username.
     * Cannot be a suspended function.
     *
     * @param username
     * @return [UserDetails]
     */
    override fun loadUserByUsername(username: String): UserDetails? = runBlocking {
        userRepositoryCached.findByUsername(username)
    }

    suspend fun findAllOnly10(): List<User> {
        log.info { "Obtaining All Users" }
        return userRepositoryCached.findAllOnly10().toList()
    }

    /**
     * Register Function for new Users
     *
     * @param userDtoRegister
     * @return [User] or a [UserException]
     */
    suspend fun register(userDtoRegister: UserDTORegister): Result<User, UserException> {
        log.info { "Registering User with username: ${userDtoRegister.username}" }

        val usernameUsed = userRepositoryCached.findByUsername(userDtoRegister.username)

        return if (usernameUsed == null) {
            val emailUsed = userRepositoryCached.findByEmail(userDtoRegister.email)

            if (emailUsed == null) {
                val user = userDtoRegister.toUser()

                val userNew = user.copy(
                    password = passwordEncoder.encode(user.password)
                )

                Ok(userRepositoryCached.save(userNew))

            } else Err(UserDataBaseConflict("Email already used"))
        } else Err(UserDataBaseConflict("Username already used"))
    }

    /**
     * Creation Function for Admins to create/register new Users by them self's
     *
     * @param userDTOCreate
     * @return [User] or [UserException]
     */
    suspend fun create(userDTOCreate: UserDTOCreate): Result<User, UserException> {
        log.info { "Creating User with username: ${userDTOCreate.username}" }

        val usernameUsed = userRepositoryCached.findByUsername(userDTOCreate.username)

        return if (usernameUsed == null) {
            val emailUsed = userRepositoryCached.findByEmail(userDTOCreate.email)

            if (emailUsed == null) {
                val user = userDTOCreate.toUser()

                val userNew = user.copy(
                    password = passwordEncoder.encode(user.password)
                )

                Ok(userRepositoryCached.save(userNew))

            } else Err(UserDataBaseConflict("Email already used"))
        } else Err(UserDataBaseConflict("Username already used"))
    }

    /**
     * Function to search a user by username
     *
     * @param username
     * @return [User] or [UserExceptionNotFound]
     */
    suspend fun findByUsername(username: String): Result<User, UserException> {
        log.info { "Finding User with username: $username" }

        val user = userRepositoryCached.findByUsername(username)
        return if (user == null) {
            Err(UserExceptionNotFound("User not found with username: $username"))
        } else Ok(user)
    }

    /**
     * Function to obtain a profile for a User
     *
     * @param user
     * @return [UserDTOProfile] or [UserExceptionNotFound]
     */
    suspend fun findUserProfile(user: User): Result<UserDTOProfile, UserException> {
        log.info { "Finding Score by User: ${user.username} for Profile" }

        val userSearched = userRepositoryCached.findByUsername(user.username)

        return if (userSearched == null) {
            Err(UserExceptionNotFound("User not found with username: ${user.username}"))
        } else {
            val score = scoreRepositoryCached.findByUserId(userSearched.id!!)

            if (score == null) {
                Ok(user.toDTOProfile(null))
            } else {
                Ok(user.toDTOProfile(score.toScoreDTO()))
            }
        }
    }

    /**
     * Function that obtains an X page for a User with an associated score
     *
     * @param page
     * @param size
     * @param sortBy
     * @return [Page] of [UserDTOLeaderBoard] or [UserExceptionNotFound]
     */
    suspend fun findAllForLeaderBoard(
        page: Int,
        size: Int,
        sortBy: String
    ): Result<Page<UserDTOLeaderBoard>, UserException> {
        log.info { "Obtaining users for LeaderBoard" }

        val pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, sortBy)
        val pageFound = userRepositoryCached.findUsersForLeaderBoard(pageRequest).firstOrNull()

        return if (pageFound == null) {
            Err(UserExceptionNotFound("Page ${pageRequest.pageNumber} not found"))
        } else Ok(pageFound)
    }

    /**
     * Function to update a password of a user.
     *
     * @param user
     * @param userDTOPasswordUpdate
     * @return True or [UserExceptionBadRequest]
     */
    suspend fun updatePassword(
        user: User,
        userDTOPasswordUpdate: UserDTOPasswordUpdate
    ): Result<Boolean, UserException> {
        log.info { "User with username: ${user.username} is trying to update the password" }
        val updatedPassword: String

        return if (!passwordEncoder.matches(userDTOPasswordUpdate.actualPassword, user.password)) {
            log.info { "Failed in first step" }
            Err(UserExceptionBadRequest("The Actual Password is not correct."))

        } else if (passwordEncoder.matches(userDTOPasswordUpdate.newPassword, user.password)) {
            log.info { "Failed in second step" }
            Err(UserExceptionBadRequest("The new password is the same as the old password"))

        } else {
            log.info { "Updating the password..." }

            updatedPassword = passwordEncoder.encode(userDTOPasswordUpdate.newPassword)
            val userUpdated = user.copy(
                password = updatedPassword
            )

            userRepositoryCached.save(userUpdated)

            Ok(true)
        }

    }

    /**
     * Function to delete an existing user.
     *
     * @param username
     * @return [User] or [UserExceptionNotFound]
     */
    suspend fun delete(username: String): Result<User, UserException> {
        log.info { "Deleting user with username: $username" }

        val user = userRepositoryCached.findByUsername(username)

        return if (user == null) {
            Err(UserExceptionNotFound("User not found with username: $username"))
        } else {
            scoreRepositoryCached.deleteByUserId(user.id!!)

            Ok(userRepositoryCached.deleteById(user.id)!!)
        }
    }

// -- SCORES --

    /**
     * Function to search a score using the given user ID
     *
     * @param userId
     * @return A possible [Score]
     */
    suspend fun findScoreByUserId(userId: UUID): Score? {
        log.info { "Searching Score for user: $userId" }

        return scoreRepositoryCached.findByUserId(userId)
    }

    /**
     * Function to save/update a score
     *
     * @param userId
     * @param scoreNumber
     * @return Boolean
     */
    suspend fun saveScore(userId: String, scoreNumber: String): Boolean {
        log.info { "Saving Score for user: $userId" }

        var updated = false
        var actualScore = scoreRepositoryCached.findByUserId(UUID.fromString(userId))

        if (actualScore == null) {
            actualScore = Score(
                userId = UUID.fromString(userId),
                scoreNumber = 0,
                dateObtained = LocalDate.now()
            )
        }

        if (actualScore.scoreNumber < scoreNumber.toLong()) {
            log.info { "SCORE UPDATED" }
            updated = true

            val score = ScoreDTOCreate(
                userId = userId,
                scoreNumber = scoreNumber
            )
            scoreRepositoryCached.deleteByUserId(UUID.fromString(userId))

            scoreRepositoryCached.save(score.toScore())
        } else log.info { "SCORE NOT UPDATED" }

        return updated
    }

}