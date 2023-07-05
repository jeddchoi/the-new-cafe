package io.github.jeddchoi.data.util

import java.util.Locale



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


    object PasswordValidator {
        private const val PASSWORD_MIN_LENGTH = 8

        fun isSecure(password: String): Boolean {
            if (password.length < PASSWORD_MIN_LENGTH) {
                return false
            }
            val categories = arrayOf(
                Regex("[A-Z]"),  // 영어 대문자
                Regex("[a-z]"),  // 영어 소문자
                Regex("[0-9]"),  // 숫자
                Regex("[^A-Za-z0-9]")  // 특수문자
            )
            var numCategories = 0
            for (category in categories) {
                if (category.containsMatchIn(password)) {
                    numCategories++
                }
            }
            if (numCategories < 3) {
                return false
            }
            val commonPasswords = listOf("password", "123456", "qwerty")  // 자주 사용되는 패스워드 목록
            if (commonPasswords.contains(password.lowercase(Locale.ROOT))) {
                return false
            }
            return true
        }
    }

}


