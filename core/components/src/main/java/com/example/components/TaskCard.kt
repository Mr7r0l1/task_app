package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.TaskInfo
import com.example.design.DeleteButtonColor
import com.example.design.DynamicText
import com.example.design.EditButtonColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TaskCard(
    taskInfo: TaskInfo,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(10.dp)
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(4.dp)) {
        Box() {
            Column(Modifier.padding(innerPadding)) {
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(taskInfo.taskTitle)
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
            }
        }
    }
}

@Composable
fun SwipeableTaskCard(
    taskInfo: TaskInfo,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    onErase: () -> Unit,
    visible: Boolean
) {
    val scope = rememberCoroutineScope()

    val slideAmount = 165.dp // Adjust to width of buttons
    val density = LocalDensity.current

    val slidePx = with(density) { slideAmount.toPx() }
    val offsetX = remember { Animatable(0f) }
    val globalOffset = remember { Animatable(0f) }
    var isSlid by remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically() + fadeOut(),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(globalOffset.value.roundToInt(), 0) }
                .height(IntrinsicSize.Min)) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = EditButtonColor),
                        onClick = { /* Handle edit */ },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            tint = Color.White,
                            contentDescription = "Edit"
                        )
                    }
                    var buttonActive: Boolean by remember { mutableStateOf(true) }
                    Button(
                        enabled = buttonActive, onClick = {
                            scope.launch {
                                buttonActive = false
                                globalOffset.animateTo(targetValue = -with(density) { 1000.dp.toPx() }, animationSpec = tween(300))
                                onErase()
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = DeleteButtonColor)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            tint = Color.White,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
            val interactionSource = remember { MutableInteractionSource() }
            Card(
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .fillMaxWidth()

                    .clickable(interactionSource = interactionSource, indication = null) {
                        scope.launch {
                            if (isSlid) {
                                offsetX.animateTo(0f)
                            } else {
                                offsetX.animateTo(-slidePx)
                            }
                            isSlid = !isSlid
                        }
                    }, elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(innerPadding)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier
                            .clipToBounds()
                            .weight(1f)
                        ){
                            Text(
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationX = -offsetX.value
                                    },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                text = taskInfo.taskTitle
                            )
                        }
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
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskCard(
    task: TaskInfo,
    onErase: () -> Unit,
    isErased: Boolean
) {
    AnimatedVisibility(
        visible = !isErased,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(),
        enter = expandVertically(animationSpec = tween(300)) + fadeIn()
    ) {
        Column {
            SwipeableTaskCard(
                taskInfo = task,
                onErase = onErase,
                visible = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}