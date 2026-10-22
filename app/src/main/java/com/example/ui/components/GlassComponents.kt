package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FrostedSilver
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassDarkBgEnd
import com.example.ui.theme.GlassDarkBgStart
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfacePressed
import com.example.ui.theme.PlatinumWhite
import com.example.ui.theme.SteelGrey

/**
 * Full screen glass backdrop with deep graphite-charcoal gradient and frosted specular highlights.
 */
@Composable
fun GlassBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .background(
        Brush.verticalGradient(
          colors = listOf(
            GlassDarkBgStart,
            Color(0xFF121418),
            GlassDarkBgEnd
          )
        )
      )
  ) {
    content()
  }
}

/**
 * Elegant frosted glass card with metallic specular border.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  backgroundColor: Color = GlassSurface,
  borderStroke: BorderStroke = BorderStroke(
    1.dp,
    Brush.verticalGradient(
      colors = listOf(
        GlassBorder,
        GlassBorderSubtle
      )
    )
  ),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(shape)
      .background(backgroundColor)
      .border(borderStroke, shape)
  ) {
    content()
  }
}

/**
 * Animated tactile glass button with bouncy spring scale on press and frosted aesthetic.
 */
@Composable
fun GlassButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  text: String? = null,
  icon: ImageVector? = null,
  isPrimary: Boolean = true,
  shape: Shape = RoundedCornerShape(16.dp),
  testTag: String = "glass_button"
) {
  var isPressed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.93f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "glass_btn_scale"
  )

  val bgBrush = if (isPrimary) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0x38FFFFFF),
        Color(0x1AFFFFFF)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color(0x18FFFFFF),
        Color(0x0AFFFFFF)
      )
    )
  }

  val textColor = if (isPrimary) PlatinumWhite else FrostedSilver

  Box(
    modifier = modifier
      .testTag(testTag)
      .scale(scale)
      .clip(shape)
      .background(bgBrush)
      .border(
        BorderStroke(
          1.dp,
          Brush.verticalGradient(
            colors = listOf(
              if (isPrimary) Color(0x45FFFFFF) else Color(0x22FFFFFF),
              Color(0x10FFFFFF)
            )
          )
        ),
        shape
      )
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDown()
          isPressed = true
          do {
            val event = awaitPointerEvent()
          } while (event.changes.any { it.pressed })
          isPressed = false
        }
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 18.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = text,
          tint = textColor,
          modifier = Modifier.size(18.dp)
        )
        if (text != null) {
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
      if (text != null) {
        Text(
          text = text,
          color = textColor,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.3.sp
        )
      }
    }
  }
}

/**
 * Animated glass icon button with spring scale.
 */
@Composable
fun GlassIconButton(
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String,
  size: Dp = 44.dp,
  tint: Color = PlatinumWhite,
  shape: Shape = CircleShape,
  testTag: String = "glass_icon_button"
) {
  var isPressed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.88f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "glass_icon_btn_scale"
  )

  Box(
    modifier = modifier
      .testTag(testTag)
      .size(size)
      .scale(scale)
      .clip(shape)
      .background(
        Brush.verticalGradient(
          colors = listOf(
            if (isPressed) GlassSurfacePressed else Color(0x20FFFFFF),
            Color(0x0CFFFFFF)
          )
        )
      )
      .border(
        BorderStroke(
          1.dp,
          Brush.verticalGradient(
            colors = listOf(
              Color(0x3AFFFFFF),
              Color(0x10FFFFFF)
            )
          )
        ),
        shape
      )
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDown()
          isPressed = true
          do {
            val event = awaitPointerEvent()
          } while (event.changes.any { it.pressed })
          isPressed = false
        }
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = tint,
      modifier = Modifier.size(20.dp)
    )
  }
}

/**
 * Frosted Glass Chip for filters and categories.
 */
@Composable
fun GlassChip(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  count: Int? = null,
  testTag: String = "glass_chip"
) {
  var isPressed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "chip_scale"
  )

  val bgBrush = if (selected) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0x40FFFFFF),
        Color(0x26FFFFFF)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color(0x12FFFFFF),
        Color(0x08FFFFFF)
      )
    )
  }

  val borderColor = if (selected) Color(0x55FFFFFF) else Color(0x1AFFFFFF)
  val textColor = if (selected) PlatinumWhite else SteelGrey
  val shape = RoundedCornerShape(14.dp)

  Box(
    modifier = modifier
      .testTag(testTag)
      .scale(scale)
      .clip(shape)
      .background(bgBrush)
      .border(BorderStroke(1.dp, borderColor), shape)
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDown()
          isPressed = true
          do {
            val event = awaitPointerEvent()
          } while (event.changes.any { it.pressed })
          isPressed = false
        }
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 14.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = text,
        color = textColor,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
      )
      if (count != null) {
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) Color(0x33FFFFFF) else Color(0x1AFFFFFF))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = count.toString(),
            color = if (selected) PlatinumWhite else SteelGrey,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Animated Glass Progress Bar with frosted track and glowing silver progress indicator.
 */
@Composable
fun GlassProgressBar(
  progress: Float, // 0f..1f
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioNoBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "progress_bar_anim"
  )

  Box(
    modifier = modifier
      .height(10.dp)
      .clip(CircleShape)
      .background(Color(0x14FFFFFF))
      .border(BorderStroke(0.5.dp, Color(0x1FFFFFFF)), CircleShape)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(animatedProgress)
        .height(10.dp)
        .clip(CircleShape)
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color(0x60FFFFFF),
              PlatinumWhite
            )
          )
        )
    )
  }
}
