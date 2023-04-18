package resa.mario.services

import kotlinx.coroutines.flow.firstOrNull
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
import resa.mario.exceptions.UserException.UserExceptionBadRequest
import resa.mario.exceptions.UserException.UserExceptionNotFound
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
    override fun loadUserByUsername(username: String): UserDetails = runBlocking {
        userRepositoryCached.findByUsername(username) ?: throw UserExceptionNotFound("USER NOT FOUND")
    }

    /**
     * Register Function for new Users
     *
     * @param userDtoRegister
     * @return [User]
     */
    suspend fun register(userDtoRegister: UserDTORegister): User {
        log.info { "Registering User with username: ${userDtoRegister.username}" }
        val user = userDtoRegister.toUser()

        val userNew = user.copy(
            password = passwordEncoder.encode(user.password)
        )

        return userRepositoryCached.save(userNew)
    }

    /**
     * Creation Function for Admins to create/register new Users by them self's
     *
     * @param userDTOCreate
     * @return [User]
     */
    suspend fun create(userDTOCreate: UserDTOCreate): User {
        log.info { "Creating User with username: ${userDTOCreate.username}" }

        val user = userDTOCreate.toUser()

        val userNew = user.copy(
            password = passwordEncoder.encode(user.password)
        )

        return userRepositoryCached.save(userNew)
    }

    /**
     * Function to search a user by username
     *
     * @param username
     * @return [User]
     */
    suspend fun findByUsername(username: String): User {
        log.info { "Finding User with username: $username" }

        return userRepositoryCached.findByUsername(username)
            ?: throw UserExceptionNotFound("User not found with username: $username")
    }

    /**
     * Function to obtain a profile for a User
     *
     * @param user
     * @return [UserDTOProfile]
     */
    suspend fun findUserProfile(user: User): UserDTOProfile {
        log.info { "Finding Score by User: ${user.username} for Profile" }

        val score = scoreRepositoryCached.findByUserId(user.id!!)

        return if (score == null) {
            user.toDTOProfile(null)
        } else {
            user.toDTOProfile(score.toScoreDTO())
        }
    }

    /**
     * Function that obtains an X page for a User with an associated score
     *
     * @param page
     * @param size
     * @param sortBy
     * @return [Page] of [UserDTOLeaderBoard]
     */
    suspend fun findAllForLeaderBoard(page: Int, size: Int, sortBy: String): Page<UserDTOLeaderBoard> {
        log.info { "Obtaining users for LeaderBoard" }

        val pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, sortBy)
        return userRepositoryCached.findUsersForLeaderBoard(pageRequest).firstOrNull()
            ?: throw UserExceptionNotFound("Page $page not found")
    }

    /**
     * Function to update a password of a user.
     *
     * @param user
     * @param userDTOPasswordUpdate
     * @return Boolean
     */
    suspend fun updatePassword(user: User, userDTOPasswordUpdate: UserDTOPasswordUpdate): Boolean {
        log.info { "User with username: ${user.username} is trying to update the password" }

        val updatedPassword: String

        if (!passwordEncoder.matches(userDTOPasswordUpdate.actualPassword, user.password)) {
            log.info { "Error updating password" }
            throw UserExceptionBadRequest("The Actual Password is not correct.")

        } else if (passwordEncoder.matches(userDTOPasswordUpdate.newPassword, user.password) ||
            passwordEncoder.matches(userDTOPasswordUpdate.repeatNewPassword, user.password)
        ) {
            throw UserExceptionBadRequest("The new password is the same as the old password")
        } else {
            log.info { "Updating password" }
            updatedPassword = passwordEncoder.encode(userDTOPasswordUpdate.newPassword)
        }

        val userUpdated = user.copy(
            password = updatedPassword
        )

        userRepositoryCached.save(userUpdated)

        return true
    }

    /**
     * Function to delete an existing user.
     *
     * @param username
     * @return [User]
     */
    suspend fun delete(username: String): User {
        log.info { "Deleting user with username: $username" }

        val user =
            userRepositoryCached.findByUsername(username)
                ?: throw UserExceptionNotFound("1.User with username: $username not found")

        scoreRepositoryCached.deleteByUserId(user.id!!)

        return userRepositoryCached.deleteById(user.id)
            ?: throw UserExceptionNotFound("2.User with username: $username not found")
    }

// -- SCORES --

    /**
     * Function to search a score using the given username
     *
     * @param userId
     * @return A possible [Score]
     */
    suspend fun findScoreByUsername(userId: UUID): Score? {
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