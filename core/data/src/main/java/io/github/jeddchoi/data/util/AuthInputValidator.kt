package io.github.jeddchoi.data.util

import java.util.regex.Pattern


object AuthInputValidator {

    fun isValidEmail(email: String): Boolean {
        return EmailValidator.isValidEmail(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return PasswordValidator.isSecure(password)
    }

    fun isNameValid(name: String): Boolean {
        return NameValidator.isValidName(name)
    }

    fun doPasswordsMatch(password: String?, confirmPassword: String): Boolean {
        return password == confirmPassword
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

        fun isValidEmail(email: String): Boolean {
            return email.isNotBlank() && EMAIL_REGEX.toRegex().matches(email)
        }
    }


    object NameValidator {
        private const val NAME_REGEX = "^[a-zA-Z ]+\$"
        private const val MAX_NAME_LENGTH = 50

        fun isValidName(name: String): Boolean {
            return name.isNotBlank() && name.matches(NAME_REGEX.toRegex()) && name.length <= MAX_NAME_LENGTH
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
        fun isSecure(password: String): Boolean {
            val matcher = pattern.matcher(password);
            return matcher.matches();
        }
    }

}


