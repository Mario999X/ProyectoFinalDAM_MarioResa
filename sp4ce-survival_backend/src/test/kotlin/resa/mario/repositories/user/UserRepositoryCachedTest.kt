package resa.mario.repositories.user

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepository
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class UserRepositoryCachedTest {

    private val score = Score(
        UUID.randomUUID(),
        UUID.randomUUID(),
        28,
        LocalDate.now()
    )

    private val user = User(
        UUID.randomUUID(),
        "user_test",
        "123456",
        "test@test.com",
        User.UserRole.ADMIN,
        LocalDate.now()
    )

    @MockK
    private lateinit var repository: UserRepository

    @MockK
    private lateinit var scoreRepository: ScoreRepository

    @InjectMockKs
    private lateinit var repositoryCached: UserRepositoryCached

    init {
        MockKAnnotations.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findAllOnly10() = runTest {
        coEvery { repository.findFirst10ByOrderByRole(any()) } returns flowOf(user)

        val result = repositoryCached.findAllOnly10()

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result[0].username, user.username) }
        )

        coVerify { repository.findFirst10ByOrderByRole(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByUsername() = runTest {
        coEvery { repository.findByUsername(any()) } returns flowOf(user)

        val result = repositoryCached.findByUsername(user.username)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result!!.username, user.username) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByUsernameNotFound() = runTest {
        coEvery { repository.findByUsername(any()) } returns emptyFlow()

        val result = repositoryCached.findByUsername(user.username)

        assertAll(
            { assertNull(result) }
        )

        coVerify { repository.findByUsername(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByEmail() = runTest {
        coEvery { repository.findByEmail(any()) } returns flowOf(user)

        val result = repositoryCached.findByEmail(user.email)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result!!.username, user.username) }
        )

        coVerify { repository.findByEmail(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByEmailNotFound() = runTest {
        coEvery { repository.findByEmail(any()) } returns emptyFlow()

        val result = repositoryCached.findByEmail(user.email)

        assertAll(
            { assertNull(result) }
        )

        coVerify { repository.findByEmail(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findUsersForLeaderBoard() = runTest {
        coEvery { scoreRepository.findAllBy(any()) } returns flowOf(score)
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.count() } returns 1

        val pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "scoreNumber")
        val result = repositoryCached.findUsersForLeaderBoard(pageRequest).toList()

        assertAll(
            { assertNotNull(result) },
            { assertEquals(user.username, result[0].get().toList()[0].username) }
        )

        coVerify { scoreRepository.findAllBy(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun save() = runTest {
        coEvery { repository.save(any()) } returns user

        val result = repositoryCached.save(user)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result.username, user.username) }
        )

        coVerify { repository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteById() = runTest {
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.deleteById(any()) } returns Unit

        val result = repositoryCached.deleteById(user.id!!)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result!!.username, user.username) }
        )

        coVerify { repository.findById(any()) }
        coVerify { repository.deleteById(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val result = repositoryCached.deleteById(user.id!!)

        assertAll(
            { assertNull(result) },
        )

        coVerify { repository.findById(any()) }
    }
}