package org.filmix.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class IntPagingSource<T : Any>(
    private val getter: suspend (page: Int?) -> IntPage<T>
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>) = try {
        val response = getter(params.key)

        LoadResult.Page(
            data = response.items,
            prevKey = null,
            nextKey = response.next
        )
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null
}

open class IntPage<T>(
    open val items: List<T> = emptyList(),
    open val next: Int? = null
)