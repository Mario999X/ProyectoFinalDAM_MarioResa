package resa.mario.repositories.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import resa.mario.dto.UserDTOLeaderBoard
import resa.mario.mappers.toDTOLeaderBoard
import resa.mario.mappers.toScoreDTO
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepository
import java.util.*

/**
 * Repository Class that will execute the Crud operations from the repository through the interface implemented
 *
 * @property repository [UserRepository]
 * @property scoreRepository [ScoreRepository]
 */
@Repository
class UserRepositoryCached
@Autowired constructor(
    private val repository: UserRepository,
    private val scoreRepository: ScoreRepository
) : IUserRepository {

    /**
     * Function to obtain first 10 users in the database, sorted by role
     *
     * @return A possible list with 10 users
     */
    override suspend fun findAllOnly10(): List<User> = withContext(Dispatchers.IO) {
        return@withContext repository.findFirst10ByOrderByRole(User.UserRole.ADMIN.name).toList()
    }

    /**
     * Function that using the user´s username to search that unique user.
     *
     * @param username
     * @return A possible [User]
     */
    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        return@withContext repository.findByUsername(username).firstOrNull()
    }

    /**
     * Function that using the user´s email to search that unique user.
     *
     * @param email
     * @return A possible [User]
     */
    override suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        return@withContext repository.findByEmail(email).firstOrNull()
    }

    /**
     * Function that returns a flow of a page of users with a score. They are order by the score number value.
     *
     * @param page
     * @return Flow of page of [UserDTOLeaderBoard]
     */
    override suspend fun findUsersForLeaderBoard(page: PageRequest): Flow<Page<UserDTOLeaderBoard>> =
        withContext(Dispatchers.IO) {
            var position = if (page.pageNumber == 0) 0 else 10 * page.pageNumber

            return@withContext scoreRepository.findAllBy(page).toList().map {
                position++
                val user = it.userId.let { id -> repository.findById(id) }
                user!!.toDTOLeaderBoard(position.toString(), it.toScoreDTO())
            }
                .windowed(page.pageSize, page.pageSize, true)
                .map { PageImpl(it, page, repository.count()) }
                .asFlow()
        }

    /**
     * Function that will save the user into the database
     *
     * @param user
     * @return [User]
     */
    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        return@withContext repository.save(user)
    }

    /**
     * Function that will delete a user from the database through the userID
     *
     * @param id
     * @return A possible [User]
     */
    override suspend fun deleteById(id: UUID): User? = withContext(Dispatchers.IO) {
        val user = repository.findById(id) ?: return@withContext null
        user.id?.let { repository.deleteById(it) }

        return@withContext user
    }
}