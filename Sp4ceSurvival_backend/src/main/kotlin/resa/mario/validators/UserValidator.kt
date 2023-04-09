package resa.mario.validators

import resa.mario.dto.UserDTOCreate
import resa.mario.dto.UserDTOLogin
import resa.mario.dto.UserDTOPasswordUpdate
import resa.mario.dto.UserDTORegister
import resa.mario.exceptions.UserExceptionBadRequest

fun UserDTORegister.validate(): UserDTORegister {
    if (this.username.isBlank())
        throw UserExceptionBadRequest("Username must not be blank.")
    else if (this.email.isBlank())
        throw UserExceptionBadRequest("Email must not be blank.")
    else if (!this.email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")))
        throw UserExceptionBadRequest("Invalid email.")
    else if (this.password.length < 5 || this.password.isBlank())
        throw UserExceptionBadRequest("Password must at least be 5 characters long.")
    else if (this.repeatPassword != this.password)
        throw UserExceptionBadRequest("Password and Repeat Password must be the same.")
    else return this
}

fun UserDTOCreate.validate(): UserDTOCreate {
    if (this.username.isBlank())
        throw UserExceptionBadRequest("Username must not be blank.")
    else if (this.email.isBlank())
        throw UserExceptionBadRequest("Email must not be blank.")
    else if (!this.email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")))
        throw UserExceptionBadRequest("Invalid email.")
    else if (this.password.length < 5 || this.password.isBlank())
        throw UserExceptionBadRequest("Password must at least be 5 characters long.")
    else return this
}

fun UserDTOLogin.validate(): UserDTOLogin {
    if (this.username.isBlank())
        throw UserExceptionBadRequest("Username must not be blank.")
    else if (this.password.length < 5 || this.password.isBlank())
        throw UserExceptionBadRequest("Password must at least be 5 characters long.")
    else return this
}

fun UserDTOPasswordUpdate.validate(): UserDTOPasswordUpdate {
    if (this.newPassword != this.repeatNewPassword)
        throw UserExceptionBadRequest("New password must be the same in both new cases.")
    else if (this.newPassword.isBlank() || this.repeatNewPassword.isBlank())
        throw UserExceptionBadRequest("Both cases must not be blank.")
    else if (this.newPassword.length < 5 || this.repeatNewPassword.length < 5)
        throw UserExceptionBadRequest("Password must at least be 5 characters long.")
    else return this
}