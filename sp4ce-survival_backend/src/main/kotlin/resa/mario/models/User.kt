package resa.mario.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import java.util.UUID

/**
 * User model
 *
 * @property id Main ID, UUID
 * @property username Username of User, String
 * @property password Password of User, String
 * @property email Email of User, String
 * @property role Role of User, [UserRole]
 * @property createdAt Creation Date, LocalDate
 */
@Table(name = "users")
data class User(
    @Id
    val id: UUID? = null,

    @get:JvmName("username")
    val username: String,
    @get:JvmName("userPassword")

    val password: String,
    val email: String,
    val role: UserRole,
    @Column("created_at")
    val createdAt: LocalDate,
) : UserDetails {

    /**
     * Enum class for the User Roles
     *
     */
    enum class UserRole {
        USER, ADMIN
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authority = SimpleGrantedAuthority("ROLE_${role.name}")
        return mutableListOf<GrantedAuthority>(authority)
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}