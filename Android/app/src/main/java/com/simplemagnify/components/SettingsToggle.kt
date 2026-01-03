package com.simplemagnify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simplemagnify.models.AppSettings
import com.simplemagnify.models.ButtonPosition
import com.simplemagnify.utils.Constants

@Composable
fun SettingsToggle(
    title: String,
    isOn: Boolean,
    settings: AppSettings,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Constants.Dimensions.cornerRadius))
            .background(settings.buttonBackgroundColor)
            .clickable { onToggle(!isOn) }
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .semantics {
                contentDescription = title
                stateDescription = if (isOn) "On" else "Off"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = Constants.FontSizes.label,
            color = settings.textColor
        )

        BigToggle(
            isOn = isOn,
            settings = settings,
            onToggle = onToggle
        )
    }
}

@Composable
fun BigToggle(
    isOn: Boolean,
    settings: AppSettings,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isOn) settings.buttonColor else Constants.Colors.sliderTrack)
            .clickable { onToggle(!isOn) },
        contentAlignment = if (isOn) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun SettingsSegmentedControl(
    title: String,
    selection: ButtonPosition,
    settings: AppSettings,
    onSelectionChange: (ButtonPosition) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = Constants.FontSizes.label,
            color = settings.textColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Dimensions.buttonSpacing)
        ) {
            ButtonPosition.values().forEach { position ->
                val isSelected = selection == position
                val backgroundColor = if (isSelected) settings.buttonColor else settings.buttonBackgroundColor
                val textColor = if (isSelected) settings.backgroundColor else settings.textColor

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(Constants.Dimensions.cornerRadius))
                        .background(backgroundColor)
                        .clickable { onSelectionChange(position) }
                        .semantics {
                            contentDescription = "${position.name} hand mode"
                            stateDescription = if (isSelected) "Selected" else "Not selected"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (position == ButtonPosition.LEFT) Constants.Strings.LEFT else Constants.Strings.RIGHT,
                        fontSize = Constants.FontSizes.button,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
