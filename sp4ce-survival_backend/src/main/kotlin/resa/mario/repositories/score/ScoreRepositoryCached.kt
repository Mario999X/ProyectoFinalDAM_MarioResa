package resa.mario.repositories.score

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import resa.mario.models.Score
import resa.mario.repositories.user.UserRepository
import java.util.*

@Repository
class ScoreRepositoryCached
@Autowired constructor(
    private val repository: ScoreRepository,
    private val userRepository: UserRepository
) : IScoreRepository {

    @Cacheable("scores")
    override suspend fun findByUserId(userId: UUID): Score? = withContext(Dispatchers.IO) {
        return@withContext repository.findByUserId(userId).firstOrNull()
    }

    @CachePut("scores")
    override suspend fun save(score: Score): Score = withContext(Dispatchers.IO) {
        return@withContext repository.save(score)
    }

    @CacheEvict("scores")
    override suspend fun deleteByUserId(userId: UUID): Score? = withContext(Dispatchers.IO) {
        userRepository.findById(userId) ?: return@withContext null

        val score = repository.findByUserId(userId).firstOrNull()

        if (score != null) {
            repository.delete(score)
        }

        return@withContext score
    }
}