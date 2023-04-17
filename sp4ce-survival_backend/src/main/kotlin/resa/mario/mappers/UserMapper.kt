package resa.mario.mappers

import resa.mario.dto.*
import resa.mario.models.User
import java.time.LocalDate

/**
 * Mapper for [User] to [UserDTOProfile]
 *
 * @param score Possible [ScoreDTOResponse] associated
 * @return [UserDTOProfile]
 */
fun User.toDTOProfile(score: ScoreDTOResponse?): UserDTOProfile {
    return UserDTOProfile(
        username = username,
        email = email,
        createdAt = createdAt.toString(),
        score
    )
}

/**
 * Mapper for [User] to [UserDTOLeaderBoard]
 *
 * @param position User position according to their score
 * @param score [ScoreDTOResponse] associated
 * @return [UserDTOLeaderBoard]
 */
fun User.toDTOLeaderBoard(position: String, score: ScoreDTOResponse): UserDTOLeaderBoard {
    return UserDTOLeaderBoard(
        position,
        username = username,
        score
    )
}

/**
 * Mapper for [User] to [UserDTOResponse]
 *
 * @return [UserDTOResponse]
 */
fun User.toDTOResponse(): UserDTOResponse {
    return UserDTOResponse(
        username = username,
    )
}

/**
 * Mapper for [UserDTORegister] to [User]
 *
 * @return [User]
 */
fun UserDTORegister.toUser(): User {
    return User(
        username = username,
        password = password,
        email = email,
        role = User.UserRole.USER,
        createdAt = LocalDate.now(),
    )
}

/**
 * Mapper for [UserDTOCreate] to [User]
 *
 * @return [User]
 */
fun UserDTOCreate.toUser(): User {
    return User(
        username = username,
        password = password,
        email = email,
        role = role.let {
            if (role == "USER") {
                User.UserRole.USER
            } else User.UserRole.ADMIN
        },
        createdAt = LocalDate.now(),
    )
}