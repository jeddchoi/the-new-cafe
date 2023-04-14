package io.github.jeddchoi.data.repository.fake

import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeAuthRepositoryImpl @Inject constructor() : AuthRepository {
    private var currentUser: User? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        // 이메일과 비밀번호로 인증하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 로그인한 사용자 정보를 저장합니다.
        delay(4_000)
        currentUser = User(email, "John", "Doe")
        return Result.success(Unit)
    }

    override suspend fun registerWithEmail(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): Result<Unit> {
        // 이메일과 비밀번호로 사용자를 등록하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 등록한 사용자 정보를 저장합니다.
        delay(4_000)
        currentUser = User(email, firstName, lastName)
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        // Google Sign-In으로 인증하는 가짜 로직을 작성합니다.
        // 성공한 경우에는 currentUser에 로그인한 사용자 정보를 저장합니다.
        delay(4_000)
        currentUser = User("johndoe@gmail.com", "John", "Doe")
        return Result.success(Unit)
    }

    override suspend fun logout() {
        // currentUser에서 현재 사용자 정보를 지웁니다.
        delay(4_000)
        currentUser = null
    }

    override fun isUserSignedIn(): Boolean {
        // currentUser가 null이 아닌 경우에는 사용자가 로그인한 상태입니다.
        return currentUser != null
    }

    override fun getCurrentUser(): Flow<User?> {
        // currentUser가 null이 아닌 경우에는 사용자 ID를 반환합니다.
        return flow { emit(currentUser) }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        // 비밀번호 재설정 이메일을 보내는 가짜 로직을 작성합니다.
        delay(4_000)
        return Result.success(Unit)
    }
}
