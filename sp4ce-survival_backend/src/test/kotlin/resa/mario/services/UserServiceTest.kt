package resa.mario.services

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.security.crypto.password.PasswordEncoder
import resa.mario.dto.UserDTOCreate
import resa.mario.dto.UserDTOPasswordUpdate
import resa.mario.dto.UserDTORegister
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepositoryCached
import resa.mario.repositories.user.UserRepositoryCached
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class UserServiceTest {

    private val userDtoCreate = UserDTOCreate(
        "test_user",
        "123455",
        "test@test.com",
        User.UserRole.ADMIN.name
    )

    private val userDtoRegister = UserDTORegister(
        userDtoCreate.username,
        userDtoCreate.password,
        userDtoCreate.password,
        userDtoCreate.email
    )


    private val user = User(
        UUID.randomUUID(),
        userDtoCreate.username,
        password = userDtoCreate.password,
        userDtoCreate.email,
        User.UserRole.ADMIN,
        LocalDate.now()
    )

    private val userDtoUpdate = UserDTOPasswordUpdate(
        user.password,
        "123456",
        "123456"
    )

    private val score = Score(
        UUID.randomUUID(),
        user.id!!,
        200,
        LocalDate.now()
    )

    @MockK
    private lateinit var repository: UserRepositoryCached

    @MockK
    private lateinit var scoreRepository: ScoreRepositoryCached

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMockKs
    private lateinit var service: UserService

    init {
        MockKAnnotations.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadUserByUsername() = runTest {
        coEvery { repository.findByUsername(any()) } returns user

        val result = service.loadUserByUsername(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findAllOnly10() = runTest {
        coEvery { repository.findAllOnly10() } returns listOf(user)

        val result = service.findAllOnly10()

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result[0].username, user.username) }
        )

        coVerify { repository.findAllOnly10() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun register() = runTest {
        coEvery { repository.findByUsername(any()) } returns null
        coEvery { repository.findByEmail(any()) } returns null
        coEvery { passwordEncoder.encode(any()) } returns userDtoRegister.password
        coEvery { repository.save(any()) } returns user

        val result = service.register(userDtoRegister)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(userDtoRegister.username, result.component1()!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { repository.findByEmail(any()) }
        coVerify { passwordEncoder.encode(any()) }
        coVerify { repository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun registerFailedByUsernameUsed() = runTest {
        coEvery { repository.findByUsername(any()) } returns user

        val result = service.register(userDtoRegister)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("Username already used", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun registerFailedByEmailUsed() = runTest {
        coEvery { repository.findByUsername(any()) } returns null
        coEvery { repository.findByEmail(any()) } returns user

        val result = service.register(userDtoRegister)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("Email already used", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { repository.findByEmail(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create() = runTest {
        coEvery { repository.findByUsername(any()) } returns null
        coEvery { repository.findByEmail(any()) } returns null
        coEvery { passwordEncoder.encode(any()) } returns userDtoCreate.password
        coEvery { repository.save(any()) } returns user

        val result = service.create(userDtoCreate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(userDtoCreate.username, result.component1()!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { repository.findByEmail(any()) }
        coVerify { passwordEncoder.encode(any()) }
        coVerify { repository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createFailedByUsernameUsed() = runTest {
        coEvery { repository.findByUsername(any()) } returns user

        val result = service.create(userDtoCreate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("Username already used", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createFailedByEmailUsed() = runTest {
        coEvery { repository.findByUsername(any()) } returns null
        coEvery { repository.findByEmail(any()) } returns user

        val result = service.create(userDtoCreate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("Email already used", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { repository.findByEmail(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByUsername() = runTest {
        coEvery { repository.findByUsername(any()) } returns user

        val result = service.findByUsername(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(userDtoCreate.username, result.component1()!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByUsernameNotFound() = runTest {
        coEvery { repository.findByUsername(any()) } returns null

        val result = service.findByUsername(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("User not found with username: ${user.username}", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUserProfile() = runTest {
        coEvery { repository.findByUsername(any()) } returns user
        coEvery { scoreRepository.findByUserId(any()) } returns score

        val result = service.findUserProfile(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result.component1()!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { scoreRepository.findByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUserProfileNoScore() = runTest {
        coEvery { repository.findByUsername(any()) } returns user
        coEvery { scoreRepository.findByUserId(any()) } returns null

        val result = service.findUserProfile(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result.component1()!!.username) },
            { assertNull(result.component1()!!.score) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { scoreRepository.findByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUserProfileNotFound() = runTest {
        coEvery { repository.findByUsername(any()) } returns null

        val result = service.findUserProfile(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("User not found with username: ${user.username}", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findAllForLeaderBoardEmpty() = runTest {
        coEvery { repository.findUsersForLeaderBoard(any()) } returns flowOf(Page.empty())

        val result = service.findAllForLeaderBoard(99, 10, "scoreNumber").component1()!!.toList()

        assertAll(
            { assertNotNull(result) },
            { assertTrue(result.isEmpty()) }
        )

        coVerify { repository.findUsersForLeaderBoard(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePassword() = runTest {
        coEvery { passwordEncoder.matches(any(), any()) } returns true
        coEvery { passwordEncoder.matches(userDtoUpdate.newPassword, any()) } returns false
        coEvery { passwordEncoder.encode(any()) } returns userDtoUpdate.newPassword
        coEvery { repository.save(any()) } returns user

        val result = service.updatePassword(user, userDtoUpdate)

        assertAll(
            { assertNotNull(result) },
            { assertTrue(result.component1()!!) }
        )

        coVerify { passwordEncoder.matches(any(), any()) }
        coVerify { passwordEncoder.matches(userDtoUpdate.newPassword, any()) }
        coVerify { passwordEncoder.encode(any()) }
        coVerify { repository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePasswordActualPasswordIncorrect() = runTest {
        coEvery { passwordEncoder.matches(any(), any()) } returns false

        val result = service.updatePassword(user, userDtoUpdate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("The Actual Password is not correct.", result.component2()!!.message) }
        )

        coVerify { passwordEncoder.matches(any(), any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePasswordNewPasswordSameAsOldOne() = runTest {
        coEvery { passwordEncoder.matches(any(), any()) } returns true
        coEvery { passwordEncoder.matches(userDtoUpdate.newPassword, any()) } returns true

        val result = service.updatePassword(user, userDtoUpdate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("The new password is the same as the old password", result.component2()!!.message) }
        )

        coVerify { passwordEncoder.matches(any(), any()) }
        coVerify { passwordEncoder.matches(userDtoUpdate.newPassword, any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun delete() = runTest {
        coEvery { repository.findByUsername(any()) } returns user
        coEvery { scoreRepository.deleteByUserId(any()) } returns score
        coEvery { repository.deleteById(any()) } returns user

        val result = service.delete(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(userDtoCreate.username, result.component1()!!.username) }
        )

        coVerify { repository.findByUsername(any()) }
        coVerify { scoreRepository.deleteByUserId(any()) }
        coVerify { repository.deleteById(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteNotFound() = runTest {
        coEvery { repository.findByUsername(any()) } returns null

        val result = service.delete(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("User not found with username: ${user.username}", result.component2()!!.message) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findScoreByUsername() = runTest {
        coEvery { scoreRepository.findByUserId(any()) } returns score

        val result = service.findScoreByUserId(user.id!!)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(score.scoreNumber, result!!.scoreNumber) }
        )

        coVerify { scoreRepository.findByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findScoreByUsernameNotFound() = runTest {
        coEvery { scoreRepository.findByUserId(any()) } returns null

        val result = service.findScoreByUserId(user.id!!)

        assertAll(
            { assertNull(result) }
        )

        coVerify { scoreRepository.findByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun saveScore() = runTest {
        coEvery { scoreRepository.findByUserId(any()) } returns null
        coEvery { scoreRepository.deleteByUserId(any()) } returns score
        coEvery { scoreRepository.save(any()) } returns score

        val result = service.saveScore(user.id.toString(), score.scoreNumber.toString())

        assertAll(
            { assertNotNull(result) },
            { assertTrue(result) }
        )

        coVerify { scoreRepository.findByUserId(any()) }
        coVerify { scoreRepository.deleteByUserId(any()) }
        coVerify { scoreRepository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun saveScoreFailed() = runTest {
        coEvery { scoreRepository.findByUserId(any()) } returns score

        val result = service.saveScore(user.id.toString(), score.scoreNumber.toString())

        assertAll(
            { assertNotNull(result) },
            { assertFalse(result) }
        )

        coVerify { scoreRepository.findByUserId(any()) }
    }
}