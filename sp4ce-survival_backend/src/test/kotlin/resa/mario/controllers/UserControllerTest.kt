package resa.mario.controllers

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.security.authentication.AuthenticationManager
import resa.mario.config.security.jwt.JwtTokensUtils
import resa.mario.dto.UserDTOCreate
import resa.mario.dto.UserDTOLogin
import resa.mario.dto.UserDTOPasswordUpdate
import resa.mario.dto.UserDTORegister
import resa.mario.exceptions.UserExceptionBadRequest
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
        User.UserRole.ADMIN
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
        coEvery { service.register(any()) } returns user
        coEvery { jwtTokensUtils.create(any()) } returns ""

        val result = controller.register(userDtoRegister).body

        assertAll(
            { assertNotNull(result) },
            { assertEquals("", result) }
        )

        coVerify { service.register(any()) }
        coVerify { jwtTokensUtils.create(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create() = runTest {
        coEvery { service.create(any()) } returns user
        coEvery { jwtTokensUtils.create(any()) } returns ""

        val result = controller.create(userDtoCreate, user2).body

        assertAll(
            { assertNotNull(result) },
            { assertEquals("", result) }
        )

        coVerify { service.create(any()) }
        coVerify { jwtTokensUtils.create(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loginFailed() = runTest {
        val result = assertThrows<UserExceptionBadRequest> {
            controller.login(userDTOLoginBad)
        }

        assertAll(
            { assertEquals("Username must not be blank.", result.message) }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUserByUsername() = runTest {
        coEvery { service.findByUsername(any()) } returns user

        val result = controller.findUserByUsername(user2, user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result.body!!.username) }
        )

        coVerify { service.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPagingForLeaderBoard() = runTest {
        coEvery { service.findAllForLeaderBoard(any(), any(), any()) } returns Page.empty()

        val result = controller.getPagingForLeaderBoard(user2).body!!.toList()

        assertAll(
            { assertNotNull(result) },
            { assertTrue(result.isEmpty()) }
        )

        coVerify { service.findAllForLeaderBoard(any(), any(), any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findMe() = runTest {
        coEvery { service.findScoreByUserId(any()) } returns user.toDTOProfile(score.toScoreDTO())

        val result = controller.findMe(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result.body!!.username) }
        )

        coVerify { service.findScoreByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateScore() = runTest {
        coEvery { service.saveScore(any(), any()) } returns true

        val result = controller.updateScore(user, score.scoreNumber.toString())

        assertAll(
            { assertNotNull(result) },
            { assertEquals("SCORE UPDATED", result.body) }
        )

        coVerify { service.saveScore(any(), any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePassword() = runTest {
        coEvery { service.updatePassword(any(), any()) } returns true

        val result = controller.updatePassword(user, userDtoUpdate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals("USER UPDATED", result.body) }
        )

        coVerify { service.updatePassword(any(), any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteMe() = runTest {
        coEvery { service.delete(any()) } returns user

        val result = controller.deleteMe(user)

        assertAll(
            { assertNotNull(result) }
        )

        coVerify { service.delete(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createUserInitializer() = runTest {
        coEvery { service.create(any()) } returns user

        val result = controller.createUserInitializer(userDtoCreate)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result.username) }
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