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
        email = "mario999@gmail.com",
        role = User.UserRole.ADMIN.name,
    ),
    UserDTOCreate(
        username = "Sebas111",
        password = "12345",
        email = "sebas111@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Loli112",
        password = "12345",
        email = "loli112@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Ivan113",
        password = "12345",
        email = "ivan113@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Dylan114",
        password = "12345",
        email = "dylan114@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Nahuel115",
        password = "12345",
        email = "nahuel115@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Dani116",
        password = "12345",
        email = "dani116@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Victor117",
        password = "12345",
        email = "test117@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Laura118",
        password = "12345",
        email = "test118@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Test119",
        password = "12345",
        email = "test119@gmail.com",
        role = User.UserRole.USER.name,
    ),
    UserDTOCreate(
        username = "Test110",
        password = "12345",
        email = "test110@gmail.com",
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
    "100",
    "12",
    "12",
    "18",
    "14",
    "15",
    "120",
    "115",
    "1190",
)