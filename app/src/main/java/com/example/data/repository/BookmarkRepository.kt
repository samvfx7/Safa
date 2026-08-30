package com.example.data.repository

import com.example.data.local.dao.BookmarkDao
import com.example.data.local.entity.BookmarkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookmarkRepository(
    private val bookmarkDao: BookmarkDao
) {
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>> = bookmarkDao.getBookmarksByType(type)

    suspend fun removeBookmark(id: Long) = withContext(Dispatchers.IO) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun addBookmark(bookmark: BookmarkEntity) = withContext(Dispatchers.IO) {
        bookmarkDao.insertBookmark(bookmark)
    }
}
