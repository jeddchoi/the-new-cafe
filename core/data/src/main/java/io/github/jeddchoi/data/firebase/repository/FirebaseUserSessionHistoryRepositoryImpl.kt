package io.github.jeddchoi.data.firebase.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.github.jeddchoi.data.firebase.model.FirebaseUserSessionHistory
import io.github.jeddchoi.data.firebase.model.toUserSessionHistory
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryRepository
import io.github.jeddchoi.model.UserSessionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class FirebaseUserSessionHistoryRepositoryImpl (
    private val source: UserSessionHistoryPagingSource,
    private val config: PagingConfig
) : UserSessionHistoryRepository {
    override fun getHistories(): Flow<PagingData<UserSessionHistory>> = Pager(
        config = config
    ) {
        source
    }.flow.flowOn(Dispatchers.IO)
}


class UserSessionHistoryPagingSource(
    private val currentUserRepository: CurrentUserRepository,
    private val db: DatabaseReference
) : PagingSource<DataSnapshot, UserSessionHistory>() {
    override fun getRefreshKey(state: PagingState<DataSnapshot, UserSessionHistory>): DataSnapshot? =
        null

    override suspend fun load(params: LoadParams<DataSnapshot>): LoadResult<DataSnapshot, UserSessionHistory> = try {
        val currentUserId =
            currentUserRepository.getUserId() ?: throw RuntimeException("User not signed in")
        val queryUserSessionHistoryNames =
            db.child("seatFinder/history/$currentUserId").orderByKey()
                .limitToFirst(20)
        val currentPage = params.key ?: queryUserSessionHistoryNames.get().await()
        val lastVisibleUserSessionHistoryKey = currentPage.children.last().key
        val nextPage = queryUserSessionHistoryNames.startAfter(lastVisibleUserSessionHistoryKey).get().await()

        val products = currentPage.children.mapNotNull { snapshot ->
            snapshot.key?.let {
                snapshot.getValue(FirebaseUserSessionHistory::class.java)?.toUserSessionHistory(it)
            }
        }
        LoadResult.Page(
            data = products,
            prevKey = null,
            nextKey = nextPage
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}