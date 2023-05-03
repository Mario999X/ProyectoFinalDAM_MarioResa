package resa.mario.controllers

import com.github.michaelbull.result.toResultOr
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import resa.mario.config.security.jwt.JwtTokensUtils
import resa.mario.dto.*
import resa.mario.exceptions.UserException.*
import resa.mario.mappers.toDTOProfile
import resa.mario.mappers.toScoreDTO
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.services.UserService
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class UserControllerTest {

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

    private val userDtoUpdate = UserDTOPasswordUpdate(
        userDtoCreate.password,
        "123456",
        "123456"
    )

    private val userDTOLoginBad = UserDTOLogin(
        "",
        userDtoCreate.password
    )

    private val userDTOLoginBad2 = UserDTOLogin(
        userDtoCreate.username,
        ""
    )

    private val user = User(
        UUID.randomUUID(),
        userDtoCreate.username,
        userDtoCreate.email,
        userDtoCreate.password,
        User.UserRole.ADMIN,
        LocalDate.now()
    )
    private val user2 = User(
        UUID.randomUUID(),
        "test_user2",
        "test2@test.com",
        userDtoCreate.password,
        User.UserRole.ADMIN,
        LocalDate.now()
    )

    private val score = Score(
        UUID.randomUUID(),
        user.id!!,
        200,
        LocalDate.now()
    )

    private val emptyPage = Page.empty<UserDTOLeaderBoard>()

    @MockK
    private lateinit var service: UserService

    @MockK
    private lateinit var authenticationManager: AuthenticationManager

    @MockK
    private lateinit var jwtTokensUtils: JwtTokensUtils

    @InjectMockKs
    private lateinit var controller: UserController

    init {
        MockKAnnotations.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun register() = runTest {
        coEvery { service.register(any()) } returns user.toResultOr { UserDataBaseConflict("") }
        coEvery { jwtTokensUtils.create(any()) } returns ""

        val result = controller.register(userDtoRegister).body

        assertAll(
            { assertNotNull(result) },
            { assertEquals("", result!!.value) }
        )

        coVerify { service.register(any()) }
        coVerify { jwtTokensUtils.create(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create() = runTest {
        coEvery { service.create(any()) } returns user.toResultOr { UserDataBaseConflict("") }
        coEvery { jwtTokensUtils.create(any()) } returns ""

        val result = controller.create(userDtoCreate, user2).body

        assertAll(
            { assertNotNull(result) },
            { assertEquals("", result!!.value) }
        )

        coVerify { service.create(any()) }
        coVerify { jwtTokensUtils.create(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loginFailed() = runTest {
        val result = controller.login(userDTOLoginBad)

        assertAll(
            { assertEquals("Username must not be blank.", result.body!!.value) }
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loginFailed2() = runTest {
        val result = controller.login(userDTOLoginBad2)

        assertAll(
            { assertEquals("Password must at least be 5 characters long.", result.body!!.value) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUserByUsername() = runTest {
        coEvery { service.findByUsername(any()) } returns user.toResultOr { UserExceptionBadRequest("") }

        val result = controller.findUserByUsername(user2, user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(HttpStatus.OK, result.statusCode) }
        )

        coVerify { service.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPagingForLeaderBoard() = runTest {
        coEvery {
            service.findAllForLeaderBoard(
                any(),
                any(),
                any()
            )
        } returns emptyPage.toResultOr { UserExceptionNotFound("") }

        val result = controller.getPagingForLeaderBoard(user2)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(HttpStatus.OK, result.statusCode) }
        )

        coVerify { service.findAllForLeaderBoard(any(), any(), any()) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findMe() = runTest {
        coEvery { service.findUserProfile(any()) } returns user.toDTOProfile(score.toScoreDTO())
            .toResultOr { UserExceptionNotFound("") }

        val result = controller.findMe(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(HttpStatus.OK, result.statusCode) }
        )

        coVerify { service.findUserProfile(any()) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateScore() = runTest {
        coEvery { service.saveScore(any(), any()) } returns true

        val result = controller.updateScore(user, score.scoreNumber.toString())

        assertAll(
            { assertNotNull(result) },
            { assertEquals("SCORE UPDATED", result.body!!.value) }
        )

        coVerify { service.saveScore(any(), any()) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePassword() = runTest {
        coEvery { service.updatePassword(any(), any()) } returns true.toResultOr { UserExceptionBadRequest("") }

        val result = controller.updatePassword(user, userDtoUpdate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("USER UPDATED", result.body!!.value) }
        )

        coVerify { service.updatePassword(any(), any()) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteMe() = runTest {
        coEvery { service.delete(any()) } returns user.toResultOr { UserExceptionNotFound("") }

        val result = controller.deleteMe(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(HttpStatus.NO_CONTENT, result.statusCode) }
        )

        coVerify { service.delete(any()) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getScore() = runTest {
        coEvery { service.findScoreByUserId(any()) } returns score

        val result = controller.getScore(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(score.scoreNumber.toString(), result.body!!.scoreNumber) }
        )

        coVerify { service.findScoreByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createUserInitializer() = runTest {
        coEvery { service.create(any()) } returns user.toResultOr { UserDataBaseConflict("") }

        val result = controller.createUserInitializer(userDtoCreate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result!!.username) }
        )

        coVerify { service.create(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createScoreInitializer() = runTest {
        coEvery { service.saveScore(any(), any()) } returns true

        val result = controller.createScoreInitializer(user.id!!, score.scoreNumber.toString())

        assertAll(
            { assertNotNull(result) }
        )

        coVerify { service.saveScore(any(), any()) }
    }
}