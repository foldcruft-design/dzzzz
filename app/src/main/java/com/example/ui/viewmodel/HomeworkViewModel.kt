package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BelarusianSubjectsProvider
import com.example.data.HomeworkItem
import com.example.data.HomeworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class FilterType(val title: String) {
  ALL("Все"),
  TODAY("Сегодня"),
  TOMORROW("Завтра"),
  WEEK("Неделя"),
  PENDING("К выполнению"),
  COMPLETED("Выполнено")
}

data class FilterCriteria(
  val filter: FilterType = FilterType.ALL,
  val subject: String? = null,
  val search: String = ""
)

data class HomeworkUiState(
  val items: List<HomeworkItem> = emptyList(),
  val allItems: List<HomeworkItem> = emptyList(),
  val selectedFilter: FilterType = FilterType.ALL,
  val selectedSubject: String? = null,
  val searchQuery: String = "",
  val totalCount: Int = 0,
  val completedCount: Int = 0,
  val pendingCount: Int = 0,
  val completionRatio: Float = 0f,
  val isAddDialogOpen: Boolean = false,
  val editingItem: HomeworkItem? = null
)

class HomeworkViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: HomeworkRepository

  init {
    val db = AppDatabase.getInstance(application)
    repository = HomeworkRepository(db.homeworkDao())
  }

  private val _filterCriteria = MutableStateFlow(FilterCriteria())
  private val _isAddDialogOpen = MutableStateFlow(false)
  private val _editingItem = MutableStateFlow<HomeworkItem?>(null)

  private val _dialogState = combine(_isAddDialogOpen, _editingItem) { isOpen, item ->
    isOpen to item
  }

  val uiState: StateFlow<HomeworkUiState> = combine(
    repository.allHomework,
    _filterCriteria,
    _dialogState
  ) { allList, criteria, dialog ->
    val todayEpoch = LocalDate.now().toEpochDay()
    val tomorrowEpoch = todayEpoch + 1
    val weekEndEpoch = todayEpoch + 7

    val filtered = allList.filter { item ->
      val matchesFilter = when (criteria.filter) {
        FilterType.ALL -> true
        FilterType.TODAY -> item.dueEpochDay == todayEpoch
        FilterType.TOMORROW -> item.dueEpochDay == tomorrowEpoch
        FilterType.WEEK -> item.dueEpochDay in todayEpoch..weekEndEpoch
        FilterType.PENDING -> !item.isCompleted
        FilterType.COMPLETED -> item.isCompleted
      }

      val matchesSubject = criteria.subject == null || item.subject.equals(criteria.subject, ignoreCase = true)

      val matchesSearch = if (criteria.search.isBlank()) true else {
        item.subject.contains(criteria.search, ignoreCase = true) ||
          item.task.contains(criteria.search, ignoreCase = true) ||
          item.notes.contains(criteria.search, ignoreCase = true)
      }

      matchesFilter && matchesSubject && matchesSearch
    }

    val total = allList.size
    val completed = allList.count { it.isCompleted }
    val pending = total - completed
    val ratio = if (total > 0) completed.toFloat() / total.toFloat() else 0f

    HomeworkUiState(
      items = filtered,
      allItems = allList,
      selectedFilter = criteria.filter,
      selectedSubject = criteria.subject,
      searchQuery = criteria.search,
      totalCount = total,
      completedCount = completed,
      pendingCount = pending,
      completionRatio = ratio,
      isAddDialogOpen = dialog.first,
      editingItem = dialog.second
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = HomeworkUiState()
  )

  fun setFilter(filter: FilterType) {
    _filterCriteria.value = _filterCriteria.value.copy(filter = filter)
  }

  fun setSubjectFilter(subject: String?) {
    _filterCriteria.value = _filterCriteria.value.copy(subject = subject)
  }

  fun setSearchQuery(query: String) {
    _filterCriteria.value = _filterCriteria.value.copy(search = query)
  }

  fun openAddDialog() {
    _editingItem.value = null
    _isAddDialogOpen.value = true
  }

  fun openEditDialog(item: HomeworkItem) {
    _editingItem.value = item
    _isAddDialogOpen.value = true
  }

  fun closeDialog() {
    _isAddDialogOpen.value = false
    _editingItem.value = null
  }

  fun saveHomework(
    subject: String,
    task: String,
    dueEpochDay: Long,
    priority: Int,
    notes: String
  ) {
    viewModelScope.launch {
      val category = BelarusianSubjectsProvider.subjects.find {
        it.nameRu.equals(subject, ignoreCase = true) || it.nameBy.equals(subject, ignoreCase = true)
      }?.category ?: "Предмет"

      val currentEditing = _editingItem.value
      if (currentEditing != null) {
        val updated = currentEditing.copy(
          subject = subject.trim(),
          category = category,
          task = task.trim(),
          dueEpochDay = dueEpochDay,
          priority = priority,
          notes = notes.trim()
        )
        repository.update(updated)
      } else {
        val newItem = HomeworkItem(
          subject = subject.trim(),
          category = category,
          task = task.trim(),
          dueEpochDay = dueEpochDay,
          priority = priority,
          notes = notes.trim()
        )
        repository.insert(newItem)
      }
      closeDialog()
    }
  }

  fun toggleCompleted(item: HomeworkItem) {
    viewModelScope.launch {
      repository.setCompleted(item.id, !item.isCompleted)
    }
  }

  fun deleteHomework(item: HomeworkItem) {
    viewModelScope.launch {
      repository.delete(item)
    }
  }

  fun clearCompleted() {
    viewModelScope.launch {
      repository.deleteCompleted()
    }
  }

  fun addSampleBelarusianHomework() {
    viewModelScope.launch {
      val today = LocalDate.now().toEpochDay()
      val tomorrow = today + 1
      val dayAfter = today + 2

      val samples = listOf(
        HomeworkItem(
          subject = "Беларуская мова",
          category = "Языки и литература",
          task = "Практыкаванне 142 (стар. 89), вывучыць правіла пра дзеепрыслоўі",
          dueEpochDay = today,
          priority = 1,
          notes = "У сшытку для дамашніх работ"
        ),
        HomeworkItem(
          subject = "Алгебра",
          category = "Точные науки",
          task = "§ 4, № 4.12, 4.15 (а, б), 4.19. Паўтарыць формулы скарочанага мнажэння",
          dueEpochDay = today,
          priority = 2,
          notes = "Падрыхтавацца да самастойнай работы"
        ),
        HomeworkItem(
          subject = "История Беларуси",
          category = "История и общество",
          task = "Параграф 18, адказаць на пытанні 1-5 на стар. 112",
          dueEpochDay = tomorrow,
          priority = 0,
          notes = "Паведамленне пра Францыска Скарыну"
        ),
        HomeworkItem(
          subject = "Физика",
          category = "Естественные науки",
          task = "Лабараторная работа № 3: вымярэнне магутнасці. Задачы 12, 14",
          dueEpochDay = tomorrow,
          priority = 1,
          notes = "Прынесці лінейку і калькулятар"
        ),
        HomeworkItem(
          subject = "Английский язык",
          category = "Языки и литература",
          task = "Student's Book p. 45 ex. 3 (read & translate), Workbook p. 22",
          dueEpochDay = dayAfter,
          priority = 0,
          notes = "Learn 10 new vocabulary words"
        ),
        HomeworkItem(
          subject = "Химия",
          category = "Естественные науки",
          task = "§ 8, рашыць раўнанні рэакцый у сшытку, задача № 4",
          dueEpochDay = dayAfter,
          priority = 1,
          notes = "Паўтарыць перыядычную табліцу"
        )
      )
      repository.insertAll(samples)
    }
  }
}
