package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "homework_items")
data class HomeworkItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val subject: String,
  val category: String = "Учёба",
  val task: String,
  val dueEpochDay: Long,
  val isCompleted: Boolean = false,
  val priority: Int = 0, // 0 = Normal, 1 = Important, 2 = Urgent
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
