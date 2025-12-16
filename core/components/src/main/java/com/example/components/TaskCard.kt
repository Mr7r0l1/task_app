package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.DeleteButtonColor
import com.example.design.DynamicText
import com.example.design.EditButtonColor
import com.example.design.InProgressColor
import com.example.design.Purple40
import com.example.model.TaskInfo
import com.example.utils.PreferencesManager
import com.example.utils.formatTime
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.temporal.ChronoUnit

val daysOfWeek = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
@Composable
fun QuickTaskCard(
    taskInfo: TaskInfo,
    modifier: Modifier = Modifier,
    preferencesManager: PreferencesManager,
    onView: () -> Unit,
    innerPadding: PaddingValues = PaddingValues(10.dp)
) {


    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(4.dp), onClick = {onView()}
    ) {

        Column(Modifier.padding(innerPadding)) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = taskInfo.taskTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier.background(
                        taskInfo.taskStatus.displayColor, shape = RoundedCornerShape(25)
                    )
                ) {
                    DynamicText(
                        text = taskInfo.taskStatus.GetDisplayName(),
                        backgroundColor = taskInfo.taskStatus.displayColor,
                        padding = 5
                    )
                }
            }
            if(taskInfo.reminderTimeMillis != null){
                var selectedTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES)
                taskInfo.reminderTimeMillis?.let { nonNullMillis ->
                    selectedTime = LocalTime.ofNanoOfDay(nonNullMillis * 1_000_000L).truncatedTo(ChronoUnit.MINUTES)
                }

                Column (modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        daysOfWeek.forEachIndexed { index, day ->

                            val isChecked = taskInfo.reminderDays[index] == 1
                            if(isChecked) {
                                Text(
                                    text = day,
                                    modifier = Modifier.alpha(.8f),
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(Modifier.width(5.dp))
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = ""
                        )
                        Spacer(Modifier.width(5.dp))
                        val textToShow = formatTime(selectedTime,preferencesManager)
                        Text(textToShow)
                        Spacer(Modifier.width(5.dp))
                    }
                }

            }
            
            Card(
                modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                border = CardDefaults.outlinedCardBorder(enabled = true)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    maxLines = 3,
                    text = if (!taskInfo.taskMessage.isEmpty()) taskInfo.taskMessage else "Vacio...")
            }

        }

    }
}

@Composable
fun SwipeableTaskCard(
    taskInfo: TaskInfo,
    preferencesManager: PreferencesManager,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    onErase: () -> Unit,
    onView: () -> Unit,
    visible: Boolean
) {
    val scope = rememberCoroutineScope()


    var clicked by remember { mutableStateOf(false) }
    val cornerRadius by animateFloatAsState(
        targetValue = if (clicked) 100f else 25f,
        animationSpec = tween(durationMillis = 1000),
        label = "corner_animation_percentage"
    )
    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically() + fadeOut(),
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Card(
            modifier = Modifier
                .fillMaxWidth()

                .clickable(interactionSource = interactionSource, indication = null) {
                    scope.launch {
                        clicked = !clicked
                    }
                }, elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(innerPadding)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clipToBounds()
                            .weight(1f)
                    ) {
                        Text(
                            modifier = Modifier,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = taskInfo.taskTitle
                        )
                    }

                    Box(
                        modifier = Modifier.background(
                            taskInfo.taskStatus.displayColor, shape = RoundedCornerShape(cornerRadius)
                        )
                    ) {
                        DynamicText(
                            text = taskInfo.taskStatus.GetDisplayName(),
                            backgroundColor = taskInfo.taskStatus.displayColor,
                            fontSize = 10,
                            padding = 5
                        )
                    }
                    AnimatedVisibility(
                        visible = clicked,
                        exit = shrinkHorizontally() + fadeOut(),
                        enter = expandHorizontally() + fadeIn(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(5.dp))
                            var buttonActive: Boolean by remember { mutableStateOf(true) }
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = EditButtonColor),
                                onClick = { onView()},
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveRedEye,
                                    tint = Color.White,
                                    contentDescription = "Edit"
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Button(
                                enabled = buttonActive,
                                onClick = {
                                    scope.launch {
                                        buttonActive = false
                                        onErase()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeleteButtonColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    tint = Color.White,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = clicked,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Column {
                        if(taskInfo.reminderTimeMillis != null){
                            var selectedTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES)
                            taskInfo.reminderTimeMillis?.let { nonNullMillis ->
                                selectedTime = LocalTime.ofNanoOfDay(nonNullMillis * 1_000_000L).truncatedTo(ChronoUnit.MINUTES)
                            }

                            Column (modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    daysOfWeek.forEachIndexed { index, day ->

                                        val isChecked = taskInfo.reminderDays[index] == 1
                                        if(isChecked) {
                                            Text(
                                                text = day,
                                                modifier = Modifier.alpha(.8f),
                                                fontSize = 12.sp,
                                                fontStyle = FontStyle.Italic
                                            )
                                            Spacer(Modifier.width(5.dp))
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = ""
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    val textToShow = formatTime(selectedTime,preferencesManager)
                                    Text(textToShow)
                                    Spacer(Modifier.width(5.dp))
                                }
                            }

                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            onClick = { onView() },
                            border = CardDefaults.outlinedCardBorder(enabled = true)
                        ) {

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),

                                maxLines = 3,
                                text = if (!taskInfo.taskMessage.isEmpty()) taskInfo.taskMessage else "Vacio..."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskCard(
    task: TaskInfo,preferencesManager: PreferencesManager, onErase: () -> Unit,onView: () -> Unit, isErased: Boolean
) {
    AnimatedVisibility(
        visible = !isErased,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(),
        enter = expandVertically(animationSpec = tween(300)) + fadeIn()
    ) {
        Column {
            SwipeableTaskCard(
                taskInfo = task,preferencesManager = preferencesManager, onErase = onErase, onView = onView, visible = true,
            )
        }
    }
}