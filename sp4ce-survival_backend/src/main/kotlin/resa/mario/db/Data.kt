package resa.mario.db

import resa.mario.dto.UserDTOCreate
import resa.mario.models.User

fun getUsersInit() = listOf(
    UserDTOCreate(
        username = "Mario999",
        password = "12345",
        email = "mario@gmail.com",
        role = User.UserRole.ADMIN.name,
    ),
    UserDTOCreate(
        username = "Mario111",
        password = "12345",
        email = "mario111@gmail.com",
        role = User.UserRole.USER.name,
    )
)

fun getScoresInit() = listOf(
    "1230",
    "12"
)