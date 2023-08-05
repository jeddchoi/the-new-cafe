package io.github.jeddchoi.data.repository

interface UserPresenceRepository {
    fun observeUserPresence()
    fun stopObserveUserPresence()
}