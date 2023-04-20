package resa.mario.repositories.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import resa.mario.dto.UserDTOLeaderBoard
import resa.mario.models.User
import java.util.UUID

/**
 * Interface that the Repository Class will implement
 *
 */
@Repository
interface IUserRepository {
    suspend fun findByUsername(username: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findUsersForLeaderBoard(page: PageRequest): Flow<Page<UserDTOLeaderBoard>>
    suspend fun save(user: User): User
    suspend fun deleteById(id: UUID): User?
}