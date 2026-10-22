package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {
  @Query("SELECT * FROM homework_items ORDER BY isCompleted ASC, priority DESC, dueEpochDay ASC, id DESC")
  fun getAllHomework(): Flow<List<HomeworkItem>>

  @Query("SELECT * FROM homework_items WHERE dueEpochDay = :epochDay ORDER BY isCompleted ASC, priority DESC, id DESC")
  fun getHomeworkForDay(epochDay: Long): Flow<List<HomeworkItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: HomeworkItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<HomeworkItem>)

  @Update
  suspend fun update(item: HomeworkItem)

  @Delete
  suspend fun delete(item: HomeworkItem)

  @Query("DELETE FROM homework_items WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("UPDATE homework_items SET isCompleted = :completed WHERE id = :id")
  suspend fun setCompleted(id: Long, completed: Boolean)

  @Query("DELETE FROM homework_items WHERE isCompleted = 1")
  suspend fun deleteCompleted()
}
