package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BelarusianSubjectsProvider
import com.example.ui.components.AddEditHomeworkDialog
import com.example.ui.components.GlassBackground
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassProgressBar
import com.example.ui.components.HomeworkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.FrostedSilver
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.PlatinumWhite
import com.example.ui.theme.SteelGrey
import com.example.ui.viewmodel.FilterType
import com.example.ui.viewmodel.HomeworkViewModel

@Composable
fun HomeworkScreen(
  viewModel: HomeworkViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var isSearchVisible by remember { mutableStateOf(false) }

  GlassBackground(modifier = modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
      containerColor = Color.Transparent,
      floatingActionButton = {
        // Frosted Glass Floating Action Button
        var isFabPressed by remember { mutableStateOf(false) }
        val fabScale by animateFloatAsState(
          targetValue = if (isFabPressed) 0.88f else 1.0f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
          ),
          label = "fab_scale"
        )

        Box(
          modifier = Modifier
            .testTag("add_homework_fab")
            .size(62.dp)
            .scale(fabScale)
            .clip(CircleShape)
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0x50FFFFFF),
                  Color(0x25FFFFFF)
                )
              )
            )
            .border(
              BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                  listOf(
                    Color(0x80FFFFFF),
                    Color(0x25FFFFFF)
                  )
                )
              ),
              CircleShape
            )
            .pointerInput(Unit) {
              awaitEachGesture {
                awaitFirstDown()
                isFabPressed = true
                do {
                  val event = awaitPointerEvent()
                } while (event.changes.any { it.pressed })
                isFabPressed = false
              }
            }
            .clickable(onClick = { viewModel.openAddDialog() }),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Добавить задание",
            tint = PlatinumWhite,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // App Top Bar with glass styling
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Школьный Дневник",
              color = PlatinumWhite,
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Планировщик ДЗ • Беларусь",
              color = SteelGrey,
              fontSize = 12.sp,
              fontWeight = FontWeight.Normal
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconButton(
              icon = if (isSearchVisible) Icons.Default.Clear else Icons.Default.Search,
              onClick = {
                isSearchVisible = !isSearchVisible
                if (!isSearchVisible) viewModel.setSearchQuery("")
              },
              contentDescription = "Поиск",
              size = 40.dp,
              testTag = "toggle_search_btn"
            )

            Spacer(modifier = Modifier.width(8.dp))

            GlassIconButton(
              icon = Icons.Default.AutoAwesome,
              onClick = { viewModel.addSampleBelarusianHomework() },
              contentDescription = "Загрузить примеры ДЗ",
              size = 40.dp,
              tint = FrostedSilver,
              testTag = "sample_data_btn"
            )
          }
        }

        // Expandable Search Bar
        AnimatedVisibility(visible = isSearchVisible) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp)
          ) {
            OutlinedTextField(
              value = uiState.searchQuery,
              onValueChange = { viewModel.setSearchQuery(it) },
              placeholder = {
                Text(
                  "Поиск по предмету или заданию...",
                  color = SteelGrey,
                  fontSize = 13.sp
                )
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Search,
                  contentDescription = null,
                  tint = SteelGrey,
                  modifier = Modifier.size(18.dp)
                )
              },
              trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                  Icon(
                    Icons.Default.Clear,
                    contentDescription = "Очистить",
                    tint = SteelGrey,
                    modifier = Modifier
                      .size(18.dp)
                      .clickable { viewModel.setSearchQuery("") }
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("main_search_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0x60FFFFFF),
                unfocusedBorderColor = Color(0x25FFFFFF),
                focusedTextColor = PlatinumWhite,
                unfocusedTextColor = PlatinumWhite
              ),
              shape = RoundedCornerShape(16.dp),
              singleLine = true
            )
          }
        }

        // Progress Glass Card
        GlassCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("progress_glass_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Прогресс выполнения",
                  color = PlatinumWhite,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${uiState.completedCount} из ${uiState.totalCount} заданий выполнено",
                  color = SteelGrey,
                  fontSize = 12.sp
                )
              }

              val percent = (uiState.completionRatio * 100).toInt()
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0x20FFFFFF))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "$percent%",
                  color = PlatinumWhite,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassProgressBar(progress = uiState.completionRatio)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Time / Status Filter Carousel
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(FilterType.values()) { filter ->
            val count = when (filter) {
              FilterType.ALL -> uiState.totalCount
              FilterType.PENDING -> uiState.pendingCount
              FilterType.COMPLETED -> uiState.completedCount
              else -> null
            }
            GlassChip(
              text = filter.title,
              selected = uiState.selectedFilter == filter,
              onClick = { viewModel.setFilter(filter) },
              count = count,
              testTag = "filter_${filter.name.lowercase()}"
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Belarusian Subjects Quick Chips
        val allSubjectNames = remember {
          listOf("Все предметы") + BelarusianSubjectsProvider.getSubjectNames()
        }

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(allSubjectNames) { subjName ->
            val isSelected = if (subjName == "Все предметы") {
              uiState.selectedSubject == null
            } else {
              uiState.selectedSubject == subjName
            }

            Box(
              modifier = Modifier
                .testTag("subject_chip_${subjName.replace(" ", "_")}")
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color(0x35FFFFFF) else Color(0x10FFFFFF))
                .border(
                  BorderStroke(
                    1.dp,
                    if (isSelected) Color(0x50FFFFFF) else Color(0x14FFFFFF)
                  ),
                  RoundedCornerShape(12.dp)
                )
                .clickable {
                  viewModel.setSubjectFilter(if (subjName == "Все предметы") null else subjName)
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = subjName,
                color = if (isSelected) PlatinumWhite else SteelGrey,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main List of Homework Items
        if (uiState.items.isEmpty()) {
          // Empty State
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            GlassCard(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("empty_state_card")
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0x1AFFFFFF)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = FrostedSilver,
                    modifier = Modifier.size(28.dp)
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                  text = "Нет заданий",
                  color = PlatinumWhite,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = if (uiState.totalCount == 0) {
                    "Добавьте своё первое домашнее задание или загрузите примеры по белорусской программе"
                  } else {
                    "По выбранному фильтру заданий не найдено"
                  },
                  color = SteelGrey,
                  fontSize = 13.sp,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (uiState.totalCount == 0) {
                  GlassButton(
                    onClick = { viewModel.addSampleBelarusianHomework() },
                    text = "Загрузить примеры ДЗ",
                    icon = Icons.Default.AutoAwesome,
                    isPrimary = true,
                    testTag = "empty_load_samples_btn"
                  )
                } else {
                  GlassButton(
                    onClick = { viewModel.openAddDialog() },
                    text = "Добавить задание",
                    icon = Icons.Default.Add,
                    isPrimary = true,
                    testTag = "empty_add_task_btn"
                  )
                }
              }
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .testTag("homework_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(
              items = uiState.items,
              key = { it.id }
            ) { item ->
              HomeworkCard(
                item = item,
                onToggleComplete = { viewModel.toggleCompleted(item) },
                onEdit = { viewModel.openEditDialog(item) },
                onDelete = { viewModel.deleteHomework(item) }
              )
            }
          }
        }
      }
    }

    // Add or Edit Dialog
    if (uiState.isAddDialogOpen) {
      AddEditHomeworkDialog(
        editingItem = uiState.editingItem,
        onDismiss = { viewModel.closeDialog() },
        onSave = { subject, task, dueEpoch, priority, notes ->
          viewModel.saveHomework(subject, task, dueEpoch, priority, notes)
        }
      )
    }
  }
}
