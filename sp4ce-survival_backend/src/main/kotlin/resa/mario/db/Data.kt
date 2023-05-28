package resa.mario.db

import resa.mario.dto.UserDTOCreate
import resa.mario.models.User

/**
 * Data to load when initializing the application for the first time.
 *
 */
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
    ),
    UserDTOCreate(
        username = "Mario112",
        password = "12345",
        email = "mario112@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario113",
        password = "12345",
        email = "mario113@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario114",
        password = "12345",
        email = "mario114@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario115",
        password = "12345",
        email = "mario115@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario116",
        password = "12345",
        email = "mario116@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario117",
        password = "12345",
        email = "mario117@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario118",
        password = "12345",
        email = "mario118@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario119",
        password = "12345",
        email = "mario119@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Mario110",
        password = "12345",
        email = "mario110@gmail.com",
        role = User.UserRole.USER.name,
    )
)

/**
 * Data to load when initializing the application for the first time. II
 *
 */
fun getScoresInit() = listOf(
    "1230",
    "12",
    "10",
    "12",
    "12",
    "18",
    "14",
    "15",
    "120",
    "115",
    "1190",
)