package io.github.jeddchoi.data.firebase.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.firebase.model.FirebaseUserSessionHistory
import io.github.jeddchoi.data.firebase.model.toUserSessionHistory
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryRepository
import io.github.jeddchoi.model.UserSessionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseUserSessionHistoryRepositoryImpl(
    private val currentUserRepository: CurrentUserRepository,
    private val config: PagingConfig
) : UserSessionHistoryRepository {
    override fun getHistories(): Flow<PagingData<UserSessionHistory>> =
        currentUserRepository.currentUserId.flatMapLatest {
            if (it != null) {
                Pager(
                    config = config,
                    pagingSourceFactory = { UserSessionHistoryPagingSource(it) },
                ).flow
            } else {
                flowOf(PagingData.empty())
            }
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
}


class UserSessionHistoryPagingSource(
    private val currentUserId: String,
) : PagingSource<DataSnapshot, UserSessionHistory>() {

    override fun getRefreshKey(state: PagingState<DataSnapshot, UserSessionHistory>): DataSnapshot? =null

    override suspend fun load(params: LoadParams<DataSnapshot>): LoadResult<DataSnapshot, UserSessionHistory> {
        try {
            Timber.v("✅ ${params.key}")

            val queryUserSessionHistories = Firebase.database.reference.child("seatFinder/$currentUserId/history").orderByKey()
            val currentPage = params.key ?: queryUserSessionHistories.limitToLast(PAGE_SIZE).get().await()
            val firstVisibleUserSessionHistoryKey = currentPage.children.firstOrNull()?.key

            val prevPage = queryUserSessionHistories.endBefore(firstVisibleUserSessionHistoryKey).limitToLast(
                PAGE_SIZE).get().await()


            val products = currentPage.children.mapNotNull { snapshot ->
                snapshot.key?.let {
                    snapshot.getValue(FirebaseUserSessionHistory::class.java)
                        ?.toUserSessionHistory(it)
                }
            }
            return LoadResult.Page(
                data = products,
                prevKey = prevPage,
                nextKey = null,
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}
