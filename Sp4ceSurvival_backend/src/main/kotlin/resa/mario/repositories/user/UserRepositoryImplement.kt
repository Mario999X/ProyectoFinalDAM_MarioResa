package resa.mario.repositories.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import resa.mario.dto.UserDtoLeaderBoard
import resa.mario.mappers.toDTOLeaderBoard
import resa.mario.mappers.toScoreDTO
import resa.mario.models.User
import resa.mario.repositories.score.ScoreRepository
import java.util.*

@Repository
class UserRepositoryImplement
@Autowired constructor(
    private val repository: UserRepository,
    private val scoreRepository: ScoreRepository
) : IUserRepository {

    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        return@withContext repository.findByUsername(username).firstOrNull()
    }

    override suspend fun findUsersForLeaderBoard(page: PageRequest): Flow<Page<UserDtoLeaderBoard>> =
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

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        return@withContext repository.save(user)
    }

    override suspend fun deleteById(id: UUID): User? = withContext(Dispatchers.IO) {
        val user = repository.findById(id) ?: return@withContext null
        user.id?.let { repository.deleteById(it) }

        return@withContext user
    }
}