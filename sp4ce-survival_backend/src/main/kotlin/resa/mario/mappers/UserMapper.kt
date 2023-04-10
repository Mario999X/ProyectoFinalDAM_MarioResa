package resa.mario.mappers

import resa.mario.dto.*
import resa.mario.models.User
import java.time.LocalDate

fun User.toDTOProfile(score: ScoreDTOResponse?): UserDTOProfile {
    return UserDTOProfile(
        username = username,
        email = email,
        createdAt = createdAt.toString(),
        score
    )
}

fun User.toDTOLeaderBoard(position: String, score: ScoreDTOResponse): UserDTOLeaderBoard {
    return UserDTOLeaderBoard(
        position,
        username = username,
        score
    )
}

fun User.toDTOResponse(): UserDTOResponse {
    return UserDTOResponse(
        username = username,
    )
}

fun UserDTORegister.toUser(): User? {
    return if (password != repeatPassword) null
    else User(
        username = username,
        password = password,
        email = email,
        role = User.UserRole.USER,
        createdAt = LocalDate.now(),
    )
}

fun UserDTOCreate.toUser(): User {
    return User(
        username = username,
        password = password,
        email = email,
        role = role,
        createdAt = LocalDate.now(),
    )
}