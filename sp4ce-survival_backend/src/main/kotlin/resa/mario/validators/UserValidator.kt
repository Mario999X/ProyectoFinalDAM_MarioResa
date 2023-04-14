package resa.mario.validators

import resa.mario.dto.UserDTOCreate
import resa.mario.dto.UserDTOLogin
import resa.mario.dto.UserDTOPasswordUpdate
import resa.mario.dto.UserDTORegister
import resa.mario.exceptions.UserException
import resa.mario.exceptions.UserException.*
import com.github.michaelbull.result.*

fun UserDTORegister.validate(): Result<UserDTORegister, UserException> {
    if (this.username.isBlank()) return Err(UserExceptionBadRequest("Username must not be blank."))

    if (this.email.isBlank()) return Err(UserExceptionBadRequest("Email must not be blank."))

    if (!this.email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) return Err(
        UserExceptionBadRequest("Invalid email.")
    )

    if (this.password.length < 5 || this.password.isBlank()) return Err(UserExceptionBadRequest("Password must at least be 5 characters long."))

    if (this.repeatPassword != this.password) return Err(UserExceptionBadRequest("Password and Repeat Password must be the same."))

    return Ok(this)
}

fun UserDTOCreate.validate(): Result<UserDTOCreate, UserException> {
    if (this.username.isBlank()) return Err(UserExceptionBadRequest("Username must not be blank."))

    if (this.email.isBlank()) return Err(UserExceptionBadRequest("Email must not be blank."))

    if (!this.email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) return Err(
        UserExceptionBadRequest("Invalid email.")
    )

    if (this.password.length < 5 || this.password.isBlank()) return Err(UserExceptionBadRequest("Password must at least be 5 characters long."))

    return Ok(this)
}

fun UserDTOLogin.validate(): Result<UserDTOLogin, UserException> {
    if (this.username.isBlank()) return Err(UserExceptionBadRequest("Username must not be blank."))

    if (this.password.length < 5 || this.password.isBlank()) return Err(UserExceptionBadRequest("Password must at least be 5 characters long."))

    return Ok(this)
}

fun UserDTOPasswordUpdate.validate(): Result<UserDTOPasswordUpdate, UserException> {
    if (this.newPassword != this.repeatNewPassword) return Err(UserExceptionBadRequest("New Password and Repeat New Password must be the same."))

    if (this.newPassword.isBlank() || this.repeatNewPassword.isBlank()) return Err(UserExceptionBadRequest("Both cases must not be blank."))

    if (this.newPassword.length < 5 || this.repeatNewPassword.length < 5) return Err(
        UserExceptionBadRequest("Password must at least be 5 characters long.")
    )

    return Ok(this)
}