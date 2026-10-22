package com.example.data

import kotlinx.coroutines.flow.Flow

class HomeworkRepository(private val dao: HomeworkDao) {
  val allHomework: Flow<List<HomeworkItem>> = dao.getAllHomework()

  fun getHomeworkForDay(epochDay: Long): Flow<List<HomeworkItem>> =
    dao.getHomeworkForDay(epochDay)

  suspend fun insert(item: HomeworkItem): Long = dao.insert(item)

  suspend fun insertAll(items: List<HomeworkItem>) = dao.insertAll(items)

  suspend fun update(item: HomeworkItem) = dao.update(item)

  suspend fun delete(item: HomeworkItem) = dao.delete(item)

  suspend fun deleteById(id: Long) = dao.deleteById(id)

  suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)

  suspend fun deleteCompleted() = dao.deleteCompleted()
}
