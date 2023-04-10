package resa.mario.repositories.score

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
import resa.mario.models.Score
import resa.mario.models.User
import resa.mario.repositories.user.UserRepository
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class ScoreRepositoryCachedTest {

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
    private lateinit var repository: ScoreRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var repositoryCached: ScoreRepositoryCached

    init {
        MockKAnnotations.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findByUserId() = runTest {
        coEvery { repository.findByUserId(any()) } returns flowOf(score)

        val result = repositoryCached.findByUserId(score.userId)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(score.id, result!!.id) }
        )

        coVerify { repository.findByUserId(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun save() = runTest {
        coEvery { repository.save(any()) } returns score

        val result = repositoryCached.save(score)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(score.id, result.id)}
        )

        coVerify { repository.save(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteByUserId() = runTest {
        coEvery { userRepository.findById(any()) } returns user
        coEvery { repository.findByUserId(any()) } returns flowOf(score)
        coEvery { repository.delete(any()) } returns Unit

        val result = repositoryCached.deleteByUserId(user.id!!)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(score.id, result!!.id)}
        )

        coVerify { repository.delete(any()) }
    }
}