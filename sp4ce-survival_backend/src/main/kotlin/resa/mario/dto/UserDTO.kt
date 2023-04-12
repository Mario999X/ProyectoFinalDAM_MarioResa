package resa.mario.dto

import kotlinx.serialization.Serializable
import resa.mario.models.User

/**
 * DTO for the login of a user
 *
 * @property username Username of a user, String
 * @property password Password of a user, String
 */
@Serializable
data class UserDTOLogin(
    val username: String,
    val password: String
)

/**
 * DTO for the registration of a user
 *
 * @property username Username of a user, String
 * @property password Password of a user, String
 * @property repeatPassword Confirmation of the password of a user, String
 * @property email Email of a user, String
 */
@Serializable
data class UserDTORegister(
    val username: String,
    val password: String,
    val repeatPassword: String,
    val email: String
)

/**
 * DTO for the creation of a user
 *
 * @property username Username of a user, String
 * @property password Password of a user, String
 * @property email Email of a user, String
 * @property role Role of a user, [User.UserRole]
 */
@Serializable
data class UserDTOCreate(
    val username: String,
    val password: String,
    val email: String,
    val role: User.UserRole
)

/**
 * DTO for a Generic Response
 *
 * @property username Username of a user, String
 */
@Serializable
data class UserDTOResponse(
    val username: String
)

/**
 * DTO for a Profile Response
 *
 * @property username Username of a user, String
 * @property email Email of a user, String
 * @property createdAt Creation Date, String
 * @property score Possible associated score, [ScoreDTOResponse]
 */
@Serializable
data class UserDTOProfile(
    val username: String,
    val email: String,
    val createdAt: String,
    val score: ScoreDTOResponse?
)

/**
 * DTO for a LeaderBoard Response
 *
 * @property position Position in the leaderboard, String
 * @property username Username of the user, String
 * @property score Associated score of the user, [ScoreDTOResponse]
 */
@Serializable
data class UserDTOLeaderBoard(
    val position: String,
    val username: String,
    val score: ScoreDTOResponse
)

/**
 * DTO for a Password Update Request
 *
 * @property actualPassword Actual Password of the user; comprobation, String
 * @property newPassword New password of the user, String
 * @property repeatNewPassword Confirmation of the new password of the user, String
 */
@Serializable
data class UserDTOPasswordUpdate(
    val actualPassword: String,
    val newPassword: String,
    val repeatNewPassword: String
)