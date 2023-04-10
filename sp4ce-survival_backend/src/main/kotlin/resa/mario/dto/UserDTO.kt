package resa.mario.dto

import kotlinx.serialization.Serializable
import resa.mario.models.User

@Serializable
data class UserDTOLogin(
    val username: String,
    val password: String
)

@Serializable
data class UserDTORegister(
    val username: String,
    val password: String,
    val repeatPassword: String,
    val email: String
)

@Serializable
data class UserDTOCreate(
    val username: String,
    val password: String,
    val email: String,
    val role: User.UserRole
)

@Serializable
data class UserDTOResponse(
    val username: String
)

@Serializable
data class UserDTOProfile(
    val username: String,
    val email: String,
    val createdAt: String,
    val score: ScoreDTOResponse?
)

@Serializable
data class UserDTOLeaderBoard(
    val position: String,
    val username: String,
    val score: ScoreDTOResponse
)

@Serializable
data class UserDTOPasswordUpdate(
    val actualPassword: String,
    val newPassword: String,
    val repeatNewPassword: String
)