package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BelarusianSubject
import com.example.data.BelarusianSubjectsProvider
import com.example.data.HomeworkItem
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.FrostedSilver
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.PlatinumWhite
import com.example.ui.theme.SteelGrey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHomeworkDialog(
  editingItem: HomeworkItem?,
  onDismiss: () -> Unit,
  onSave: (subject: String, task: String, dueEpochDay: Long, priority: Int, notes: String) -> Unit
) {
  val todayEpoch = remember { LocalDate.now().toEpochDay() }

  var selectedSubject by remember {
    mutableStateOf(editingItem?.subject ?: "Беларуская мова")
  }
  var taskText by remember { mutableStateOf(editingItem?.task ?: "") }
  var selectedEpochDay by remember {
    mutableLongStateOf(editingItem?.dueEpochDay ?: (todayEpoch + 1))
  }
  var selectedPriority by remember { mutableIntStateOf(editingItem?.priority ?: 0) }
  var notesText by remember { mutableStateOf(editingItem?.notes ?: "") }
  var isSubjectPickerOpen by remember { mutableStateOf(false) }
  var subjectSearchQuery by remember { mutableStateOf("") }
  var isCustomSubjectMode by remember { mutableStateOf(false) }

  val dateFormatter = remember {
    DateTimeFormatter.ofPattern("d MMMM (EE)", Locale("ru"))
  }

  val selectedDateFormatted = remember(selectedEpochDay) {
    val date = LocalDate.ofEpochDay(selectedEpochDay)
    when (selectedEpochDay - todayEpoch) {
      0L -> "Сегодня, " + date.format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))
      1L -> "Завтра, " + date.format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))
      2L -> "Послезавтра, " + date.format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))
      else -> date.format(dateFormatter)
    }
  }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .testTag("add_edit_dialog")
      .fillMaxWidth(0.96f)
      .clip(RoundedCornerShape(26.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1E2127),
            Color(0xFF14161A)
          )
        )
      )
      .border(
        BorderStroke(
          1.dp,
          Brush.verticalGradient(
            colors = listOf(
              Color(0x40FFFFFF),
              Color(0x15FFFFFF)
            )
          )
        ),
        RoundedCornerShape(26.dp)
      )
      .padding(22.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (editingItem == null) "Новое задание" else "Редактировать",
            color = PlatinumWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Белорусская школьная программа",
            color = SteelGrey,
            fontSize = 12.sp
          )
        }
        GlassIconButton(
          icon = Icons.Default.Close,
          onClick = onDismiss,
          contentDescription = "Закрыть",
          size = 36.dp,
          testTag = "dialog_close_btn"
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 480.dp)
      ) {
        // Subject Section
        item {
          Text(
            text = "ПРЕДМЕТ",
            color = SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Subject selector card
          Box(
            modifier = Modifier
              .testTag("subject_selector_card")
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0x18FFFFFF))
              .border(BorderStroke(1.dp, Color(0x22FFFFFF)), RoundedCornerShape(16.dp))
              .clickable { isSubjectPickerOpen = !isSubjectPickerOpen }
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = selectedSubject,
                  color = PlatinumWhite,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold
                )
                val byName = BelarusianSubjectsProvider.subjects.find {
                  it.nameRu.equals(selectedSubject, ignoreCase = true)
                }?.nameBy
                if (byName != null && byName != selectedSubject) {
                  Text(
                    text = byName,
                    color = SteelGrey,
                    fontSize = 12.sp
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0x20FFFFFF))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (isSubjectPickerOpen) "Скрыть" else "Выбрать предмет",
                  color = FrostedSilver,
                  fontSize = 12.sp
                )
              }
            }
          }
        }

        // Expanded Subject List
        item {
          AnimatedVisibility(visible = isSubjectPickerOpen) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF181A20))
                .border(BorderStroke(1.dp, Color(0x22FFFFFF)), RoundedCornerShape(18.dp))
                .padding(12.dp)
            ) {
              // Search input
              OutlinedTextField(
                value = subjectSearchQuery,
                onValueChange = { subjectSearchQuery = it },
                placeholder = { Text("Поиск предмета...", color = SteelGrey, fontSize = 13.sp) },
                leadingIcon = {
                  Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = SteelGrey,
                    modifier = Modifier.size(16.dp)
                  )
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("subject_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0x55FFFFFF),
                  unfocusedBorderColor = Color(0x22FFFFFF),
                  focusedTextColor = PlatinumWhite,
                  unfocusedTextColor = PlatinumWhite
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(10.dp))

              val filteredSubjects = BelarusianSubjectsProvider.subjects.filter {
                if (subjectSearchQuery.isBlank()) true else {
                  it.nameRu.contains(subjectSearchQuery, ignoreCase = true) ||
                    it.nameBy.contains(subjectSearchQuery, ignoreCase = true) ||
                    it.category.contains(subjectSearchQuery, ignoreCase = true)
                }
              }

              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                filteredSubjects.forEach { subj ->
                  val isSelected = subj.nameRu.equals(selectedSubject, ignoreCase = true)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isSelected) Color(0x40FFFFFF) else Color(0x14FFFFFF))
                      .border(
                        BorderStroke(
                          1.dp,
                          if (isSelected) Color(0x60FFFFFF) else Color(0x16FFFFFF)
                        ),
                        RoundedCornerShape(10.dp)
                      )
                      .clickable {
                        selectedSubject = subj.nameRu
                        isSubjectPickerOpen = false
                      }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = subj.nameRu,
                      color = if (isSelected) PlatinumWhite else SteelGrey,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                  }
                }
              }
            }
          }
        }

        // Task description input
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "ЧТО ЗАДАНО",
            color = SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = taskText,
            onValueChange = { taskText = it },
            placeholder = {
              Text(
                "Например: Упр. 142 (стар. 89), выучить правило",
                color = SteelGrey,
                fontSize = 14.sp
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_description_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0x55FFFFFF),
              unfocusedBorderColor = Color(0x22FFFFFF),
              focusedTextColor = PlatinumWhite,
              unfocusedTextColor = PlatinumWhite
            ),
            shape = RoundedCornerShape(14.dp),
            minLines = 2,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
          )
        }

        // Due date selector
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "СРОК СДАЧИ",
            color = SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = selectedDateFormatted,
            color = PlatinumWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val quickDates = listOf(
              "Сегодня" to todayEpoch,
              "Завтра" to (todayEpoch + 1),
              "+2 дня" to (todayEpoch + 2),
              "+1 нед." to (todayEpoch + 7)
            )

            quickDates.forEach { (label, epoch) ->
              val isSelected = selectedEpochDay == epoch
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Color(0x38FFFFFF) else Color(0x10FFFFFF))
                  .border(
                    BorderStroke(
                      1.dp,
                      if (isSelected) Color(0x50FFFFFF) else Color(0x15FFFFFF)
                    ),
                    RoundedCornerShape(12.dp)
                  )
                  .clickable { selectedEpochDay = epoch }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  color = if (isSelected) PlatinumWhite else SteelGrey,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Priority selector
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "ПРИОРИТЕТ",
            color = SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val priorities = listOf(
              0 to "Обычный",
              1 to "Важный",
              2 to "Срочно"
            )

            priorities.forEach { (code, title) ->
              val isSelected = selectedPriority == code
              val priorityBg = when (code) {
                2 -> if (isSelected) Color(0x50FFFFFF) else Color(0x12FFFFFF)
                1 -> if (isSelected) Color(0x36FFFFFF) else Color(0x10FFFFFF)
                else -> if (isSelected) Color(0x28FFFFFF) else Color(0x0CFFFFFF)
              }
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(priorityBg)
                  .border(
                    BorderStroke(
                      1.dp,
                      if (isSelected) Color(0x60FFFFFF) else Color(0x15FFFFFF)
                    ),
                    RoundedCornerShape(12.dp)
                  )
                  .clickable { selectedPriority = code }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = title,
                  color = if (isSelected) PlatinumWhite else SteelGrey,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Notes input (optional)
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "ЗАМЕТКА (НЕОБЯЗАТЕЛЬНО)",
            color = SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = notesText,
            onValueChange = { notesText = it },
            placeholder = {
              Text("В тетради для к/р, сдать на 1-м уроке...", color = SteelGrey, fontSize = 13.sp)
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("notes_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0x55FFFFFF),
              unfocusedBorderColor = Color(0x22FFFFFF),
              focusedTextColor = PlatinumWhite,
              unfocusedTextColor = PlatinumWhite
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        GlassButton(
          onClick = onDismiss,
          text = "Отмена",
          isPrimary = false,
          modifier = Modifier.weight(1f),
          testTag = "dialog_cancel_btn"
        )

        val canSave = taskText.isNotBlank() && selectedSubject.isNotBlank()

        GlassButton(
          onClick = {
            if (canSave) {
              onSave(
                selectedSubject,
                taskText,
                selectedEpochDay,
                selectedPriority,
                notesText
              )
            }
          },
          text = "Сохранить",
          isPrimary = canSave,
          modifier = Modifier.weight(1f),
          testTag = "dialog_save_btn"
        )
      }
    }
  }
}
