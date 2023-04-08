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
import resa.mario.exceptions.UserExceptionBadRequest
import resa.mario.exceptions.UserExceptionNotFound
import resa.mario.mappers.toDTOProfile
import resa.mario.mappers.toScore
import resa.mario.mappers.toScoreDTO
import resa.mario.mappers.toUser
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepositoryCached
import resa.mario.repositories.user.UserRepositoryImplement
import java.time.LocalDate
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class UserService
@Autowired constructor(
    private val userRepositoryImplement: UserRepositoryImplement,
    private val scoreRepositoryCached: ScoreRepositoryCached,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    // -- USERS --

    override fun loadUserByUsername(username: String): UserDetails = runBlocking {
        userRepositoryImplement.findByUsername(username) ?: throw UserExceptionNotFound("User not found with username: $username")
    }

    suspend fun register(userDtoRegister: UserDTORegister): User {
        log.info { "Registering User with username: ${userDtoRegister.username}" }
        try {
            val user = userDtoRegister.toUser() ?: throw UserExceptionBadRequest("Password and repeated password does not match.")

            val userNew = user.copy(
                password = passwordEncoder.encode(user.password)
            )

            return userRepositoryImplement.save(userNew)
        } catch (e: Exception) {
            throw UserExceptionBadRequest(e.message)
        }
    }

    suspend fun create(userDTOCreate: UserDTOCreate): User {
        log.info { "Creating User with username: ${userDTOCreate.username}" }

        val user = userDTOCreate.toUser()

        val userNew = user.copy(
            password = passwordEncoder.encode(user.password)
        )

        return userRepositoryImplement.save(userNew)
    }

    suspend fun findByUsername(username: String): User {
        log.info { "Finding User with username: $username" }

        return userRepositoryImplement.findByUsername(username)
            ?: throw UserExceptionBadRequest("User not found with username: $username")
    }

    suspend fun findScoreByUserId(user: User): UserDTOProfile {
        log.info { "Finding Score by User: ${user.username}" }

        val score = scoreRepositoryCached.findByUserId(user.id!!)

        return if (score == null) {
            user.toDTOProfile(null)
        } else {
            user.toDTOProfile(score.toScoreDTO())
        }
    }

    suspend fun findAllForLeaderBoard(page: Int, size: Int, sortBy: String): Page<UserDTOLeaderBoard> {
        log.info { "Obtaining users for LeaderBoard" }

        val pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, sortBy)
        return userRepositoryImplement.findUsersForLeaderBoard(pageRequest).firstOrNull()
            ?: throw UserExceptionNotFound("Page $page not found")
    }


    suspend fun updatePassword(user: User, userDTOPasswordUpdate: UserDTOPasswordUpdate): Boolean {
        log.info { "User with username: ${user.username} is trying to update the password" }

        val updatedPassword: String

        if (!passwordEncoder.matches(userDTOPasswordUpdate.actualPassword, user.password)) {
            log.info { "Error updating password" }
            throw UserExceptionBadRequest("The Actual Password is not correct.")
        } else {
            log.info { "Updating password" }
            updatedPassword = passwordEncoder.encode(userDTOPasswordUpdate.newPassword)
        }

        val userUpdated = user.copy(
            password = updatedPassword
        )

        userRepositoryImplement.save(userUpdated)

        return true
    }

    suspend fun delete(username: String): User {
        log.info { "Deleting user with username: $username" }

        val user =
            userRepositoryImplement.findByUsername(username)
                ?: throw UserExceptionNotFound("1.User with username: $username not found")

        scoreRepositoryCached.deleteByUserId(user.id!!)

        return userRepositoryImplement.deleteById(user.id)
            ?: throw UserExceptionNotFound("2.User with username: $username not found")
    }

    // -- SCORES --

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