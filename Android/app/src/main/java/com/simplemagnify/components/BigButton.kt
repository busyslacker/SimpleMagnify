package com.simplemagnify.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simplemagnify.models.AppSettings
import com.simplemagnify.utils.Constants

@Composable
fun BigButton(
    title: String,
    icon: ImageVector,
    settings: AppSettings,
    isActive: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) settings.buttonColor else settings.buttonBackgroundColor
    val contentColor = if (isActive) settings.backgroundColor else settings.buttonColor

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(Constants.Dimensions.buttonHeight)
            .semantics {
                contentDescription = "$title button${if (isActive) ", currently active" else ""}"
            },
        shape = RoundedCornerShape(Constants.Dimensions.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = Constants.FontSizes.button,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
