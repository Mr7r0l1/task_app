package com.example.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils


fun Color.isLight(): Boolean {
    // Convert Compose Color to Android ColorInt
    val colorInt = android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
    // Calculate luminance and compare to a threshold
    return ColorUtils.calculateLuminance(colorInt) > 0.5
}

@Composable
fun DynamicText(
    backgroundColor: Color,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Int = 12,
    padding: Int = 1
) {
    val textColor = if (backgroundColor.isLight()) Color.Black else Color.White

    Box(modifier = Modifier.clip(RoundedCornerShape(2.dp))){
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(padding.dp),
            fontWeight = fontWeight,
            fontSize = fontSize.sp
        )
    }
}