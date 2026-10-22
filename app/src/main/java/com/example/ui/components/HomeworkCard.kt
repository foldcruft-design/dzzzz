package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BelarusianSubjectsProvider
import com.example.data.HomeworkItem
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.FrostedSilver
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.PlatinumWhite
import com.example.ui.theme.SteelGrey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeworkCard(
  item: HomeworkItem,
  onToggleComplete: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val todayEpoch = remember { LocalDate.now().toEpochDay() }

  val dateText = remember(item.dueEpochDay) {
    val diff = item.dueEpochDay - todayEpoch
    val date = LocalDate.ofEpochDay(item.dueEpochDay)
    when (diff) {
      0L -> "Сегодня"
      1L -> "Завтра"
      -1L -> "Вчера (просрочено)"
      in -999L..-2L -> "Просрочено (${date.format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))})"
      else -> date.format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))
    }
  }

  val subjectObj = remember(item.subject) {
    BelarusianSubjectsProvider.subjects.find {
      it.nameRu.equals(item.subject, ignoreCase = true) || it.nameBy.equals(item.subject, ignoreCase = true)
    }
  }

  val isOverdue = !item.isCompleted && (item.dueEpochDay < todayEpoch)

  var isCardPressed by remember { mutableStateOf(false) }
  val cardScale by animateFloatAsState(
    targetValue = if (isCardPressed) 0.985f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "card_scale"
  )

  val cardBg = if (item.isCompleted) {
    Color(0x0EFFFFFF)
  } else when (item.priority) {
    2 -> Color(0x24FFFFFF)
    1 -> Color(0x1AFFFFFF)
    else -> Color(0x14FFFFFF)
  }

  val borderColor = if (item.isCompleted) {
    Color(0x14FFFFFF)
  } else when (item.priority) {
    2 -> Color(0x55FFFFFF)
    1 -> Color(0x35FFFFFF)
    else -> Color(0x20FFFFFF)
  }

  Box(
    modifier = modifier
      .testTag("homework_card_${item.id}")
      .fillMaxWidth()
      .scale(cardScale)
      .clip(RoundedCornerShape(20.dp))
      .background(cardBg)
      .border(
        BorderStroke(
          1.dp,
          Brush.verticalGradient(
            colors = listOf(
              borderColor,
              Color(0x0AFFFFFF)
            )
          )
        ),
        RoundedCornerShape(20.dp)
      )
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDown()
          isCardPressed = true
          do {
            val event = awaitPointerEvent()
          } while (event.changes.any { it.pressed })
          isCardPressed = false
        }
      }
      .padding(16.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top row: Subject, Code badge, Priority, Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          // Subject Short Code Pill
          val code = subjectObj?.shortCode ?: item.subject.take(3).uppercase()
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x22FFFFFF))
              .border(BorderStroke(0.5.dp, Color(0x33FFFFFF)), RoundedCornerShape(8.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = code,
              color = PlatinumWhite,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Subject full title
          Text(
            text = item.subject,
            color = if (item.isCompleted) SteelGrey else PlatinumWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          )
        }

        // Priority and Due Date
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (item.priority > 0 && !item.isCompleted) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (item.priority == 2) Color(0x40FFFFFF) else Color(0x20FFFFFF))
                .border(
                  BorderStroke(
                    0.5.dp,
                    if (item.priority == 2) Color(0x60FFFFFF) else Color(0x30FFFFFF)
                  ),
                  RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (item.priority == 2) "Срочно" else "Важно",
                color = PlatinumWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }

          // Date badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isOverdue) Color(0x30FFFFFF) else Color(0x15FFFFFF))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(
              text = dateText,
              color = if (isOverdue) PlatinumWhite else SteelGrey,
              fontSize = 11.sp,
              fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Middle Row: Checkbox + Task text
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Animated Checkbox button
        var isCheckPressed by remember { mutableStateOf(false) }
        val checkScale by animateFloatAsState(
          targetValue = if (isCheckPressed) 0.82f else 1.0f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
          ),
          label = "check_scale"
        )

        Box(
          modifier = Modifier
            .testTag("checkbox_${item.id}")
            .size(28.dp)
            .scale(checkScale)
            .clip(CircleShape)
            .background(
              if (item.isCompleted) Brush.verticalGradient(
                listOf(Color(0x60FFFFFF), Color(0x40FFFFFF))
              ) else Brush.verticalGradient(
                listOf(Color(0x15FFFFFF), Color(0x08FFFFFF))
              )
            )
            .border(
              BorderStroke(
                1.5.dp,
                if (item.isCompleted) Color(0x80FFFFFF) else Color(0x35FFFFFF)
              ),
              CircleShape
            )
            .pointerInput(Unit) {
              awaitEachGesture {
                awaitFirstDown()
                isCheckPressed = true
                do {
                  val event = awaitPointerEvent()
                } while (event.changes.any { it.pressed })
                isCheckPressed = false
              }
            }
            .clickable(onClick = onToggleComplete),
          contentAlignment = Alignment.Center
        ) {
          if (item.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Выполнено",
              tint = Color(0xFF111317),
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Task Content
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.task,
            color = if (item.isCompleted) SteelGrey else PlatinumWhite,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          )

          // Notes
          if (item.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x10FFFFFF))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Заметка: ${item.notes}",
                color = SteelGrey,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom bar: Edit and Delete buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        GlassIconButton(
          icon = Icons.Default.Edit,
          onClick = onEdit,
          contentDescription = "Редактировать",
          size = 32.dp,
          tint = SteelGrey,
          testTag = "edit_btn_${item.id}"
        )

        Spacer(modifier = Modifier.width(8.dp))

        GlassIconButton(
          icon = Icons.Default.Delete,
          onClick = onDelete,
          contentDescription = "Удалить",
          size = 32.dp,
          tint = SteelGrey,
          testTag = "delete_btn_${item.id}"
        )
      }
    }
  }
}
