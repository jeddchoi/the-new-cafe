package io.github.jeddchoi.data.util

import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.data.R
import java.util.regex.Pattern


object AuthInputValidator {
    private val emptyInputGuide = UiText.StringResource(R.string.empty_input)
    private val passwordMatchGuide = UiText.StringResource(R.string.password_isnt_same)
    private val invalidEmailGuide = UiText.StringResource(R.string.email_invalid_msg)


    fun isValidEmail(email: String): Pair<Boolean, UiText?> {
        return EmailValidator.isValidEmail(email)
    }

    fun isPasswordValid(password: String, isRegister: Boolean): Pair<Boolean, UiText?> {
        return PasswordValidator.isSecure(password, isRegister)
    }

    fun isNameValid(name: String): Pair<Boolean, UiText?> {
        return NameValidator.isValidName(name)
    }

    fun doPasswordsMatch(password: String?, confirmPassword: String): Pair<Boolean, UiText?> {
        if (confirmPassword.isBlank()) {
            return Pair(false, emptyInputGuide)
        }
        if (password != confirmPassword) {
            return Pair(false, passwordMatchGuide)
        }
        return Pair(true, null)
    }


    object EmailValidator {
        private const val EMAIL_REGEX =
            "^[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+$"

        fun isValidEmail(email: String): Pair<Boolean, UiText?> {
            if (email.isBlank()) {
                return Pair(false, emptyInputGuide)
            }
            if (EMAIL_REGEX.toRegex().matches(email).not()) {
                return Pair(false, invalidEmailGuide)
            }
            return Pair(true, null)
        }
    }


    object NameValidator {
        private const val MAX_NAME_LENGTH = 20
        private val lengthLimitGuide = UiText.StringResource(R.string.length_limit_msg, MAX_NAME_LENGTH)

        fun isValidName(name: String): Pair<Boolean, UiText?> {
            if (name.isBlank()) {
                return Pair(false, emptyInputGuide)
            }
            if (name.length > MAX_NAME_LENGTH) {
                return Pair(false, lengthLimitGuide)
            }
            return Pair(true, null)
        }
    }


    /**
     * Password validator
     *
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~])  # a special character must occur at least once you can replace with your special characters
     * (?=\\S+$)         # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     * @constructor Create empty Password validator
     */
    object PasswordValidator {
        private const val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#\$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,}$"
        private val pattern = Pattern.compile(PASSWORD_PATTERN)
        private val passwordGenerationGuide = UiText.StringResource(R.string.password_generation_guide)

        fun isSecure(password: String, isRegister: Boolean): Pair<Boolean, UiText?> {
            if (password.isBlank()) {
                return Pair(false, emptyInputGuide)
            }
            val matcher = pattern.matcher(password)
            if (isRegister && !matcher.matches()) {
                return Pair(false, passwordGenerationGuide)
            }

            return Pair(true, null)
        }
    }

}


