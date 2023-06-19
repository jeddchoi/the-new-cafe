package io.github.jeddchoi.data.repository.fake

import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.model.CurrentUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FakeAuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        // 이메일과 비밀번호로 인증하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 로그인한 사용자 정보를 저장합니다.
        delay(4_000)
        _currentUser.value = CurrentUser(email, emailAddress = "John")
        return Result.success(Unit)
    }

    override suspend fun registerWithEmail(
        email: String,
        displayName: String,
        password: String
    ): Result<Unit> {
        // 이메일과 비밀번호로 사용자를 등록하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 등록한 사용자 정보를 저장합니다.
        delay(4_000)
        _currentUser.value = CurrentUser(email, emailAddress = displayName)
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        // Google Sign-In으로 인증하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 로그인한 사용자 정보를 저장합니다.
        delay(4_000)
        _currentUser.value = CurrentUser("johndoe@gmail.com", emailAddress = "John")
        return Result.success(Unit)
    }

    override suspend fun logout() {
        // currentUser에서 현재 사용자 정보를 지웁니다.
        delay(4_000)
        _currentUser.value = null
    }


    private val _currentUser: MutableStateFlow<CurrentUser?> = MutableStateFlow(null)
    val currentUser: StateFlow<CurrentUser?>
        get() = _currentUser

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        // 비밀번호 재설정 이메일을 보내는 가짜 로직을 작성합니다.
        delay(4_000)
        return Result.success(Unit)
    }
}
